package Proyecto.Funcionalidades;

import Proyecto.EstructurasDatos.Cola;
import Proyecto.ModelosBase.Actividad;
import Proyecto.ModelosBase.Notificaciones.Notificacion;
import Proyecto.ModelosBase.Notificaciones.PrioridadNotificacion;
import Proyecto.ModelosBase.Notificaciones.TipoNotificacion;
import Proyecto.ModelosBase.Proceso;
import Proyecto.ModelosBase.Tarea;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class GestionNotificaciones {
    private static GestionNotificaciones instancia;
    private Cola<Notificacion> notificaciones;
    private Consumer<Notificacion> manejoNotificacion;

    private GestionNotificaciones() {
        this.notificaciones = new Cola<>();
    }

    public static GestionNotificaciones getInstance() {
        if (instancia == null) {
            instancia = new GestionNotificaciones();
        }
        return instancia;
    }

    public void setNotificationHandler(Consumer<Notificacion> handler) {
        this.manejoNotificacion = handler;
    }

    public void verificarTareasPendientes(Proceso proceso) {
        LocalDateTime ahora = LocalDateTime.now();

        for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
            Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
            Cola<Tarea> tareas = actividad.getTareas();

            Tarea tarea = tareas.obtenerFrente();
            while (tarea != null) {
                long horasTranscurridas = ChronoUnit.HOURS.between(tarea.getFechaCreacion(), ahora);

                if (tarea.isObligatoria() && horasTranscurridas >= 24) {
                    crearNotificacion(
                            "Tarea Vencida",
                            "La tarea '" + tarea.getDescripcion() + "' en la actividad '" +
                                    actividad.getNombre() + "' está vencida",
                            TipoNotificacion.TAREA_VENCIDA,
                            PrioridadNotificacion.ALTA,
                            proceso.getId().toString()
                    );
                } else if (horasTranscurridas >= 20 && tarea.isObligatoria()) {
                    crearNotificacion(
                            "Tarea Próxima a Vencer",
                            "La tarea '" + tarea.getDescripcion() + "' vencerá en " +
                                    (24 - horasTranscurridas) + " horas",
                            TipoNotificacion.TAREA_PROXIMA,
                            PrioridadNotificacion.MEDIA,
                            proceso.getId().toString()
                    );
                }

                tarea = tareas.desencolar();
            }
        }
    }

    public void notificarActividadIniciada(Actividad actividad, Proceso proceso) {
        crearNotificacion(
                "Actividad Iniciada",
                "La actividad '" + actividad.getNombre() + "' ha sido iniciada en el proceso '" +
                        proceso.getNombre() + "'",
                TipoNotificacion.ACTIVIDAD_INICIADA,
                actividad.isObligatoria() ? PrioridadNotificacion.ALTA : PrioridadNotificacion.MEDIA,
                proceso.getId().toString()
        );
    }

    public void notificarActividadCompletada(Actividad actividad, Proceso proceso) {
        crearNotificacion(
                "Actividad Completada",
                "La actividad '" + actividad.getNombre() + "' ha sido completada en el proceso '" +
                        proceso.getNombre() + "'",
                TipoNotificacion.ACTIVIDAD_COMPLETADA,
                PrioridadNotificacion.BAJA,
                proceso.getId().toString()
        );
    }

    public void crearNotificacion(String titulo, String mensaje, TipoNotificacion tipo, PrioridadNotificacion prioridad, String idReferencia) {
        Notificacion notificacion = new Notificacion(titulo, mensaje, tipo, prioridad, idReferencia);
        notificaciones.encolar(notificacion);

        if (manejoNotificacion != null) {
            manejoNotificacion.accept(notificacion);
        }
    }

    public Cola<Notificacion> obtenerNotificaciones() {
        return notificaciones;
    }

    public void limpiarNotificaciones() {
        while (!notificaciones.estaVacia()) {
            notificaciones.desencolar();
        }
    }

    public void notificarProcesoIniciado(Proceso proceso) {
        crearNotificacion(
                "Nuevo Proceso",
                "Se ha iniciado el proceso: " + proceso.getNombre(),
                TipoNotificacion.PROCESO_INICIADO,
                PrioridadNotificacion.MEDIA,
                proceso.getId().toString()
        );
    }

    public void notificarTareaVencida(Tarea tarea, Actividad actividad, Proceso proceso) {
        crearNotificacion(
                "Tarea Vencida",
                "La tarea '" + tarea.getDescripcion() + "' en la actividad '" +
                        actividad.getNombre() + "' ha superado su duración estimada de " +
                        tarea.getDuracion() + " horas",
                TipoNotificacion.TAREA_VENCIDA,
                PrioridadNotificacion.ALTA,
                proceso.getId().toString()
        );
    }

    public void notificarTareaProximaVencer(Tarea tarea, Actividad actividad, Proceso proceso, long horasRestantes) {
        crearNotificacion(
                "Tarea Próxima a Vencer",
                "La tarea '" + tarea.getDescripcion() + "' vencerá en " +
                        horasRestantes + " horas",
                TipoNotificacion.TAREA_PROXIMA,
                PrioridadNotificacion.MEDIA,
                proceso.getId().toString()
        );
    }

    public void notificarActividadEnRiesgo(Actividad actividad, Proceso proceso) {
        crearNotificacion(
                "Actividad en Riesgo",
                "La actividad '" + actividad.getNombre() + "' tiene tareas vencidas",
                TipoNotificacion.ACTIVIDAD_EN_RIESGO,
                PrioridadNotificacion.ALTA,
                proceso.getId().toString()
        );
    }
}