package Funcionalidades;

import EstructurasDatos.Cola;
import EstructurasDatos.Nodo;
import Interfaz.GestorNotificacionesSwing;
import ModelosBase.Actividad;
import Notificaciones.MonitorNotificaciones;
import Notificaciones.PrioridadNotificacion;
import Notificaciones.TipoNotificacion;
import ModelosBase.Proceso;
import ModelosBase.Tarea;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GestionActividades {
    GestionProcesos gestionProcesos;
    private Actividad ultimaActividad;
    private final MonitorNotificaciones monitorNotificaciones;
    private final GestionNotificaciones gestionNotificaciones;
    private final GestorNotificacionesSwing gestorNotificacionesUI;

    public GestionActividades(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
        this.ultimaActividad = null;
        this.monitorNotificaciones = MonitorNotificaciones.getInstance();
        this.gestionNotificaciones = GestionNotificaciones.getInstance();
        this.gestorNotificacionesUI = GestorNotificacionesSwing.getInstance();
    }


    public void agregarActividad(UUID idProceso, String nombre, String descripcion, boolean obligatoria) {
        Actividad nuevaActividad = new Actividad(nombre, descripcion, obligatoria);
        try {
            if (idProceso == null) {
                throw new IllegalArgumentException("El ID del proceso no puede ser null.");
            }

            Proceso proceso = gestionProcesos.buscarProceso(idProceso);
            if (proceso == null) {
                throw new IllegalStateException("Proceso no encontrado.");
            }

            monitorNotificaciones.registrarProceso(proceso); // Registro del proceso

            if (actividadExiste(idProceso, nombre)) {
                throw new IllegalStateException("Ya existe una actividad con el nombre '" + nombre + "'.");
            }

            gestionProcesos.agregarActividad(idProceso, nuevaActividad);
            ultimaActividad = nuevaActividad;

        } catch (Exception e) {
            gestionNotificaciones.crearNotificacion(
                    "Error al agregar Actividad",
                    e.getMessage(),
                    TipoNotificacion.ERROR,
                    PrioridadNotificacion.ALTA,
                    idProceso.toString()
            );
            throw e;
        }
    }

    public void insertarActividadDespuesDe(UUID id,String nombreExistente, String nombre, String descripcion, boolean obligatoria) {
        if (actividadExiste(id,nombre)) {
            System.out.println("Error: Ya existe una actividad con el nombre '" + nombre + "'.");
            return;
        }
        Actividad nuevaActividad = new Actividad(nombre, descripcion, obligatoria);
        Actividad actividadExistente = buscarActividadPorNombre(id,nombreExistente);
        if (actividadExistente != null) {
            gestionProcesos.buscarProceso(id).getActividades().insertarDespuesDe(actividadExistente, nuevaActividad);
            ultimaActividad = nuevaActividad;
            gestionNotificaciones.notificarActividadCreada(nuevaActividad, gestionProcesos.buscarProceso(id));
        } else {
            System.out.println("Error: La actividad '" + nombreExistente + "' no existe.");
        }
    }

    public void insertarActividadAlFinal(UUID id,String nombre, String descripcion, boolean obligatoria) {
        if (actividadExiste(id, nombre)) {
            System.out.println("Error: Ya existe una actividad con el nombre '" + nombre + "'.");
            return;
        }
        Actividad nuevaActividad = new Actividad(nombre, descripcion, obligatoria);
        gestionProcesos.buscarProceso(id).getActividades().insertar(nuevaActividad);
        ultimaActividad = nuevaActividad;
        gestionNotificaciones.notificarActividadCreada(nuevaActividad, gestionProcesos.buscarProceso(id));
    }

    public void insertarActividadDespuesDeUltimaCreada(UUID id,String nombre, String descripcion, boolean obligatoria) {
        if (actividadExiste(id, nombre)) {
            System.out.println("Error: Ya existe una actividad con el nombre '" + nombre + "'.");
            return;
        }
        Actividad nuevaActividad = new Actividad(nombre, descripcion, obligatoria);
        if (ultimaActividad == null) {
            gestionProcesos.buscarProceso(id).getActividades().insertar(nuevaActividad);
            gestionNotificaciones.notificarActividadCreada(nuevaActividad, gestionProcesos.buscarProceso(id));
        } else {
            gestionProcesos.buscarProceso(id).getActividades().insertarDespuesDe(ultimaActividad,nuevaActividad);
            gestionNotificaciones.notificarActividadCreada(nuevaActividad, gestionProcesos.buscarProceso(id));
        }
        ultimaActividad = nuevaActividad;
    }

    public boolean actividadExiste(UUID id, String nombre) {
        Nodo<Actividad> actual = gestionProcesos.buscarProceso(id).getActividades().getCabeza();
        while (actual != null) {
            if (actual.getValorNodo().getNombre().equals(nombre)) {
                return true;
            }
            actual = actual.getSiguienteNodo();
        }
        return false;
    }

    private Actividad buscarActividadPorNombre(UUID id,String nombre) {
        Nodo<Actividad> actual = gestionProcesos.buscarProceso(id).getActividades().getCabeza();
        while (actual != null) {
            if (actual.getValorNodo().getNombre().equals(nombre)) {
                return actual.getValorNodo();
            }
            actual = actual.getSiguienteNodo();
        }
        return null;
    }

    public void intercambiarActividades(UUID id, String nombre1, String nombre2, boolean intercambiarTareas) {
        try {
            if (nombre1.equals(nombre2)) {
                throw new IllegalArgumentException("Los nombres son iguales. No se puede realizar el intercambio.");
            }

            Actividad actividad1 = buscarActividadPorNombre(id, nombre1);
            Actividad actividad2 = buscarActividadPorNombre(id, nombre2);

            if (actividad1 == null || actividad2 == null) {
                throw new IllegalStateException("Una o ambas actividades no existen.");
            }

            // Guardar referencias a las actividades adyacentes originales
            Actividad anteriorAct1 = actividad1.getAnterior();
            Actividad siguienteAct1 = actividad1.getSiguiente();
            Actividad anteriorAct2 = actividad2.getAnterior();
            Actividad siguienteAct2 = actividad2.getSiguiente();

            // Intercambiar los enlaces de las actividades adyacentes
            // Caso especial: actividades adyacentes
            if (actividad1.getSiguiente() == actividad2) {
                // actividad1 -> actividad2
                actividad1.setAnterior(actividad2);
                actividad1.setSiguiente(siguienteAct2);
                actividad2.setAnterior(anteriorAct1);
                actividad2.setSiguiente(actividad1);

                if (anteriorAct1 != null) anteriorAct1.setSiguiente(actividad2);
                if (siguienteAct2 != null) siguienteAct2.setAnterior(actividad1);
            } else if (actividad2.getSiguiente() == actividad1) {
                // actividad2 -> actividad1
                actividad2.setAnterior(actividad1);
                actividad2.setSiguiente(siguienteAct1);
                actividad1.setAnterior(anteriorAct2);
                actividad1.setSiguiente(actividad2);

                if (anteriorAct2 != null) anteriorAct2.setSiguiente(actividad1);
                if (siguienteAct1 != null) siguienteAct1.setAnterior(actividad2);
            } else {
                // Caso general: actividades no adyacentes
                actividad1.setAnterior(anteriorAct2);
                actividad1.setSiguiente(siguienteAct2);
                actividad2.setAnterior(anteriorAct1);
                actividad2.setSiguiente(siguienteAct1);

                if (anteriorAct1 != null) anteriorAct1.setSiguiente(actividad2);
                if (siguienteAct1 != null) siguienteAct1.setAnterior(actividad2);
                if (anteriorAct2 != null) anteriorAct2.setSiguiente(actividad1);
                if (siguienteAct2 != null) siguienteAct2.setAnterior(actividad1);
            }

            // Intercambiar datos básicos
            String tempNombre = actividad1.getNombre();
            String tempDescripcion = actividad1.getDescripcion();
            boolean tempObligatoria = actividad1.isObligatoria();
            LocalDateTime tempFechaInicio = actividad1.getFechaInicio();

            actividad1.setNombre(actividad2.getNombre());
            actividad1.setDescripcion(actividad2.getDescripcion());
            actividad1.setObligatoria(actividad2.isObligatoria());
            actividad1.setFechaInicio(actividad2.getFechaInicio());

            actividad2.setNombre(tempNombre);
            actividad2.setDescripcion(tempDescripcion);
            actividad2.setObligatoria(tempObligatoria);
            actividad2.setFechaInicio(tempFechaInicio);

            // Intercambiar tareas si se solicita
            if (!intercambiarTareas) {
                // Crear copias temporales de las colas de tareas
                Cola<Tarea> tareasAct1 = new Cola<>();
                Cola<Tarea> tareasAct2 = new Cola<>();

                // Copiar las tareas de actividad1
                Cola<Tarea> colaTemporal1 = actividad1.getTareas();
                while (!colaTemporal1.estaVacia()) {
                    Tarea tarea = colaTemporal1.desencolar();
                    tareasAct1.encolar(new Tarea(tarea));  // Asumiendo que Tarea tiene un constructor de copia
                }

                // Copiar las tareas de actividad2
                Cola<Tarea> colaTemporal2 = actividad2.getTareas();
                while (!colaTemporal2.estaVacia()) {
                    Tarea tarea = colaTemporal2.desencolar();
                    tareasAct2.encolar(new Tarea(tarea));  // Asumiendo que Tarea tiene un constructor de copia
                }

                // Asignar las tareas intercambiadas
                actividad1.setTareas(tareasAct2);
                actividad2.setTareas(tareasAct1);

                // Agregar log para verificar el intercambio
                System.out.println("Intercambio de tareas realizado entre " +
                        actividad1.getNombre() + " y " + actividad2.getNombre());
            }

            notificarIntercambioActividades(actividad1, actividad2, gestionProcesos.buscarProceso(id));

        } catch (Exception e) {
            gestionNotificaciones.crearNotificacion(
                    "Error en Intercambio de Actividades",
                    e.getMessage(),
                    TipoNotificacion.ERROR,
                    PrioridadNotificacion.ALTA,
                    id.toString()
            );
            throw e;
        }
    }
    private void notificarIntercambioActividades(Actividad actividad1, Actividad actividad2, Proceso proceso) {
        String mensaje = String.format("Actividades '%s' y '%s' intercambiadas en el proceso '%s'",
                actividad1.getNombre(), actividad2.getNombre(), proceso.getNombre());
        gestionNotificaciones.crearNotificacion(
                "Intercambio de Actividades",
                mensaje,
                TipoNotificacion.ACTIVIDAD_INTERCAMBIADA,
                PrioridadNotificacion.MEDIA,
                proceso.getId().toString()
        );
    }

    public List<String> obtenerNombresActividades(UUID id) {
        List<String> nombres = new ArrayList<>();
        Nodo<Actividad> actual = gestionProcesos.buscarProceso(id).getActividades().getCabeza();

        while (actual != null) {
            nombres.add(actual.getValorNodo().getNombre());
            actual = actual.getSiguienteNodo();
        }

        return nombres;
    }


    public Actividad getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(Actividad ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

}