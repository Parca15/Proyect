package Proyecto.ModelosBase.Notificaciones;

import Proyecto.EstructurasDatos.Cola;
import Proyecto.Funcionalidades.GestionNotificaciones;
import Proyecto.ModelosBase.Actividad;
import Proyecto.ModelosBase.Proceso;
import Proyecto.ModelosBase.Tarea;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MonitorProcesos {
    private static MonitorProcesos instancia;
    private final ScheduledExecutorService scheduler;
    private final List<Proceso> procesosActivos;
    private final GestionNotificaciones gestionNotificaciones;

    private MonitorProcesos() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.procesosActivos = new ArrayList<>();
        this.gestionNotificaciones = GestionNotificaciones.getInstance();
        iniciarMonitoreo();
    }

    public static MonitorProcesos getInstance() {
        if (instancia == null) {
            instancia = new MonitorProcesos();
        }
        return instancia;
    }

    private void iniciarMonitoreo() {
        // Ejecutar verificación cada minuto
        scheduler.scheduleAtFixedRate(this::verificarEstados, 0, 1, TimeUnit.MINUTES);
    }

    public void registrarProceso(Proceso proceso) {
        procesosActivos.add(proceso);
        gestionNotificaciones.crearNotificacion(
                "Nuevo Proceso",
                "Se ha iniciado el proceso: " + proceso.getNombre(),
                TipoNotificacion.PROCESO_INICIADO,
                PrioridadNotificacion.MEDIA,
                proceso.getId().toString()
        );
    }

    private void verificarEstados() {
        LocalDateTime ahora = LocalDateTime.now();

        for (Proceso proceso : procesosActivos) {
            for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                verificarTareas(actividad, proceso, ahora);
            }
        }
    }

    private void verificarTareas(Actividad actividad, Proceso proceso, LocalDateTime ahora) {
        Cola<Tarea> tareasCopia = new Cola<>();
        boolean hayTareasVencidas = false;

        while (!actividad.getTareas().estaVacia()) {
            Tarea tarea = actividad.getTareas().desencolar();
            tareasCopia.encolar(tarea);

            long horasTranscurridas = ChronoUnit.HOURS.between(tarea.getFechaCreacion(), ahora);
            long horasRestantes = tarea.getDuracion() - horasTranscurridas;

            // Verificar si la tarea está vencida
            if (horasRestantes <= 0 && tarea.isObligatoria()) {
                gestionNotificaciones.crearNotificacion(
                        "Tarea Vencida",
                        "La tarea '" + tarea.getDescripcion() + "' en la actividad '" +
                                actividad.getNombre() + "' ha superado su duración estimada de " +
                                tarea.getDuracion() + " horas",
                        TipoNotificacion.TAREA_VENCIDA,
                        PrioridadNotificacion.ALTA,
                        proceso.getId().toString()
                );
                hayTareasVencidas = true;
            }
            // Verificar si la tarea está próxima a vencer (menos de 2 horas restantes)
            else if (horasRestantes > 0 && horasRestantes <= 2 && tarea.isObligatoria()) {
                gestionNotificaciones.crearNotificacion(
                        "Tarea Próxima a Vencer",
                        "La tarea '" + tarea.getDescripcion() + "' vencerá en " +
                                horasRestantes + " horas",
                        TipoNotificacion.TAREA_PROXIMA,
                        PrioridadNotificacion.MEDIA,
                        proceso.getId().toString()
                );
            }
        }

        // Restaurar las tareas a la cola original
        while (!tareasCopia.estaVacia()) {
            actividad.getTareas().encolar(tareasCopia.desencolar());
        }

        // Si hay tareas vencidas y la actividad es obligatoria, notificar sobre la actividad
        if (hayTareasVencidas && actividad.isObligatoria()) {
            gestionNotificaciones.crearNotificacion(
                    "Actividad en Riesgo",
                    "La actividad '" + actividad.getNombre() + "' tiene tareas vencidas",
                    TipoNotificacion.ACTIVIDAD_EN_RIESGO,
                    PrioridadNotificacion.ALTA,
                    proceso.getId().toString()
            );
        }
    }

    public void detenerMonitoreo() {
        scheduler.shutdown();
    }
}