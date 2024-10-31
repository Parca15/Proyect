package Funcionalidades;

import EstructurasDatos.Nodo;
import Interfaz.GestorNotificacionesSwing;
import ModelosBase.Actividad;
import ModelosBase.Notificaciones.MonitorNotificaciones;
import ModelosBase.Notificaciones.PrioridadNotificacion;
import ModelosBase.Notificaciones.TipoNotificacion;
import ModelosBase.Proceso;

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

    private boolean actividadExiste(UUID id, String nombre) {
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

    public void intercambiarActividades(UUID id, String nombre1, String nombre2, boolean selected) {
        try {
            if (nombre1.equals(nombre2)) {
                throw new IllegalArgumentException("Los nombres son iguales. No se puede realizar el intercambio.");
            }
            Actividad actividad1 = buscarActividadPorNombre(id, nombre1);
            Actividad actividad2 = buscarActividadPorNombre(id, nombre2);

            if (actividad1 == null || actividad2 == null) {
                throw new IllegalStateException("Una o ambas actividades no existen.");
            }

            String tempNombre = actividad1.getNombre();
            String tempDescripcion = actividad1.getDescripcion();
            boolean tempObligatoria = actividad1.isObligatoria();

            actividad1.setNombre(actividad2.getNombre());
            actividad1.setDescripcion(actividad2.getDescripcion());
            actividad1.setObligatoria(actividad2.isObligatoria());

            actividad2.setNombre(tempNombre);
            actividad2.setDescripcion(tempDescripcion);
            actividad2.setObligatoria(tempObligatoria);

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