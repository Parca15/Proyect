package Funcionalidades;

import EstructurasDatos.Cola;
import EstructurasDatos.Nodo;
import ModelosBase.Actividad;
import Notificaciones.MonitorNotificaciones;
import ModelosBase.Proceso;
import ModelosBase.Tarea;
import Notificaciones.PrioridadNotificacion;
import Notificaciones.TipoNotificacion;
import Interfaz.GestorNotificacionesSwing;
import java.util.UUID;

public class GestionTareas {
    private GestionProcesos gestionProcesos;
    private final MonitorNotificaciones monitorNotificaciones;
    private final GestionNotificaciones gestionNotificaciones;
    private final GestorNotificacionesSwing gestorNotificacionesUI;

    public GestionTareas(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
        this.monitorNotificaciones = MonitorNotificaciones.getInstance();
        this.gestionNotificaciones = GestionNotificaciones.getInstance();
        this.gestorNotificacionesUI = GestorNotificacionesSwing.getInstance();
    }

    public void agregarTarea(UUID procesoId, String nombreActividad, String descripcion, int duracion, boolean obligatoria) {
        try {
            Proceso proceso = gestionProcesos.buscarProceso(procesoId);
            if (proceso == null) {
                throw new IllegalStateException("Proceso no encontrado.");
            }

            // Registrar el proceso para monitoreo
            monitorNotificaciones.registrarProceso(proceso);

            for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                if (actividad.getNombre().equals(nombreActividad)) {
                    Cola<Tarea> tareas = actividad.getTareas();
                    Tarea nuevaTarea = new Tarea(descripcion, duracion, obligatoria);

                    if (!validarInsercionTarea(tareas, nuevaTarea, -1)) {
                        throw new IllegalStateException("No se pueden tener dos tareas opcionales consecutivas.");
                    }

                    actividad.agregarTarea(nuevaTarea);
                    gestionNotificaciones.notificarTareaCreada(nuevaTarea, actividad, proceso);
                    return;
                }
            }
            throw new IllegalStateException("Actividad no encontrada.");
        } catch (Exception e) {
            gestionNotificaciones.crearNotificacion(
                    "Error en Creación de Tarea",
                    e.getMessage(),
                    TipoNotificacion.ERROR,
                    PrioridadNotificacion.ALTA,
                    procesoId.toString()
            );
            throw e;
        }
    }

    public void insertarTareaEnPosicion(UUID procesoId, String nombreActividad, int posicion,
                                        String descripcion, int duracion, boolean obligatoria) {
        try {
            if (posicion < 0) {
                throw new IllegalStateException("La posición no puede ser negativa.");
            }

            Proceso proceso = gestionProcesos.buscarProceso(procesoId);
            if (proceso == null) {
                throw new IllegalStateException("Proceso no encontrado.");
            }

            monitorNotificaciones.registrarProceso(proceso);

            for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                if (actividad.getNombre().equals(nombreActividad)) {
                    Cola<Tarea> tareas = actividad.getTareas();
                    Tarea nuevaTarea = new Tarea(descripcion, duracion, obligatoria);

                    if (!validarInsercionTarea(tareas, nuevaTarea, posicion)) {
                        throw new IllegalStateException("No se pueden tener dos tareas opcionales consecutivas.");
                    }

                    insertarTareaEnPosicionEspecifica(tareas, nuevaTarea, posicion);
                    notificarCreacionTarea(nuevaTarea, actividad, proceso);
                    return;
                }
            }
            throw new IllegalStateException("Actividad no encontrada.");
        } catch (Exception e) {
            gestionNotificaciones.crearNotificacion(
                    "Error en Inserción de Tarea",
                    e.getMessage(),
                    TipoNotificacion.ERROR,
                    PrioridadNotificacion.ALTA,
                    procesoId.toString()
            );
            throw e;
        }
    }

    private void notificarCreacionTarea(Tarea tarea, Actividad actividad, Proceso proceso) {
        String mensaje = String.format("Nueva tarea '%s' creada en la actividad '%s' del proceso '%s'",
                tarea.getDescripcion(), actividad.getNombre(), proceso.getNombre());

        PrioridadNotificacion prioridad = tarea.isObligatoria() ?
                PrioridadNotificacion.ALTA : PrioridadNotificacion.MEDIA;

        gestionNotificaciones.crearNotificacion(
                "Nueva Tarea Creada",
                mensaje,
                TipoNotificacion.TAREA_CREADA,
                prioridad,
                proceso.getId().toString()
        );
    }

    private void insertarTareaEnPosicionEspecifica(Cola<Tarea> tareas, Tarea nuevaTarea, int posicion) {
        if (tareas.estaVacia() || posicion == 0) {
            tareas.insertarAlInicio(nuevaTarea);
            return;
        }

        int tamano = obtenerTamano(tareas);
        if (posicion >= tamano) {
            tareas.insertarAlFinal(nuevaTarea);
            return;
        }

        Nodo<Tarea> actual = tareas.getNodoPrimero();
        for (int j = 0; j < posicion - 1; j++) {
            actual = actual.getSiguienteNodo();
        }
        tareas.insertarDespuesDe(actual.getValorNodo(), nuevaTarea);
    }

    private boolean validarInsercionTarea(Cola<Tarea> tareas, Tarea nuevaTarea, int posicion) {
        if (tareas.estaVacia()) {
            return true;
        }

        Tarea[] tareasArray = convertirColaAArreglo(tareas);

        if (posicion == -1) {
            posicion = tareasArray.length;
        }

        if (!nuevaTarea.isObligatoria()) {
            if (posicion > 0 && !tareasArray[posicion - 1].isObligatoria()) {
                return false;
            }

            if (posicion < tareasArray.length && !tareasArray[posicion].isObligatoria()) {
                return false;
            }
        }

        return true;
    }

    private Tarea[] convertirColaAArreglo(Cola<Tarea> tareas) {
        int tamano = obtenerTamano(tareas);
        Tarea[] array = new Tarea[tamano];
        Nodo<Tarea> actual = tareas.getNodoPrimero();
        int index = 0;

        while (actual != null) {
            array[index++] = actual.getValorNodo();
            actual = actual.getSiguienteNodo();
        }

        return array;
    }

    private int obtenerTamano(Cola<Tarea> tareas) {
        int tamano = 0;
        Nodo<Tarea> temp = tareas.getNodoPrimero();
        while (temp != null) {
            tamano++;
            temp = temp.getSiguienteNodo();
        }
        return tamano;
    }
}