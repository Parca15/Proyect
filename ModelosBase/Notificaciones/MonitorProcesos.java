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

    private MonitorProcesos() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.procesosActivos = new ArrayList<>();
        GestionNotificaciones gestionNotificaciones = GestionNotificaciones.getInstance();
        iniciarMonitoreo();
    }

    public static MonitorProcesos getInstance() {
        if (instancia == null) {
            instancia = new MonitorProcesos();
        }
        return instancia;
    }

    private void iniciarMonitoreo() {
        scheduler.scheduleAtFixedRate(this::verificarEstados, 0, 1, TimeUnit.MINUTES);
    }

    public void registrarProceso(Proceso proceso) {
        procesosActivos.add(proceso);
        // Usar el método público de GestionNotificaciones
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

            long horasTranscurridas = ChronoUnit.HOURS.between(tarea.getFechaCreacion(), ahora);
            long horasRestantes = tarea.getDuracion() - horasTranscurridas;

            if (horasRestantes <= 0 && tarea.isObligatoria()) {
                GestionNotificaciones.getInstance().notificarTareaVencida(tarea, actividad, proceso);
                hayTareasVencidas = true;
            }
            else if (horasRestantes > 0 && horasRestantes <= 2 && tarea.isObligatoria()) {
                GestionNotificaciones.getInstance().notificarTareaProximaVencer(tarea, actividad, proceso, horasRestantes);
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