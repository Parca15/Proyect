package Notificaciones;

import EstructurasDatos.Cola;
import Funcionalidades.GestionNotificaciones;
import ModelosBase.Actividad;
import ModelosBase.Proceso;
import ModelosBase.Tarea;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MonitorNotificaciones {
    private static MonitorNotificaciones instancia;
    private final ScheduledExecutorService scheduler;
    private final List<Proceso> procesosActivos;

    private MonitorNotificaciones() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.procesosActivos = new ArrayList<>();
        GestionNotificaciones gestionNotificaciones = GestionNotificaciones.getInstance();
        iniciarMonitoreo();
    }

    public static MonitorNotificaciones getInstance() {
        if (instancia == null) {
            instancia = new MonitorNotificaciones();
        }
        return instancia;
    }

    private void iniciarMonitoreo() {
        scheduler.scheduleAtFixedRate(this::verificarEstados, 0, 5, TimeUnit.MINUTES);
    }

    public void registrarProceso(Proceso proceso) {
        procesosActivos.add(proceso);
        GestionNotificaciones.getInstance().notificarProcesoIniciado(proceso);
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

            long minutosTranscurridos = ChronoUnit.MINUTES.between(tarea.getFechaCreacion(), ahora);
            long minutosRestantes = tarea.getDuracion() - minutosTranscurridos;

            if (minutosRestantes <= 0 && tarea.isObligatoria()) {
                GestionNotificaciones.getInstance().notificarTareaVencida(tarea, actividad, proceso);
                tareasCopia.desencolar();
                hayTareasVencidas = true;
            }
            else if (minutosRestantes > 0 && minutosRestantes <= 30 && tarea.isObligatoria()) {
                GestionNotificaciones.getInstance().notificarTareaProximaVencer(tarea, actividad, proceso, minutosRestantes);
            }
        }

        // Restaurar las tareas a la cola original
        while (!tareasCopia.estaVacia()) {
            actividad.getTareas().encolar(tareasCopia.desencolar());
        }

        if (hayTareasVencidas && actividad.isObligatoria()) {
            GestionNotificaciones.getInstance().notificarActividadEnRiesgo(actividad, proceso);
        }
    }

    public void detenerMonitoreo() {
        scheduler.shutdown();
    }
}