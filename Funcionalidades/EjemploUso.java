package Proyecto.Funcionalidades;

import Proyecto.ModelosBase.Actividad;
import Proyecto.ModelosBase.Proceso;
import Proyecto.ModelosBase.Tarea;
import Proyecto.ModelosBase.Notificaciones.MonitorProcesos;
import Proyecto.ModelosBase.Inter.GestorNotificacionesSwing;

import java.util.concurrent.TimeUnit;

public class EjemploUso {
    public static void main(String[] args) {
        // Inicializar el gestor de notificaciones Swing
        GestorNotificacionesSwing.getInstance();

        // Crear un proceso
        Proceso desarrollo = new Proceso("Desarrollo de Software");

        // Crear actividades con duración estimada
        Actividad planificacion = new Actividad(
                "Planificación",
                "Fase de planificación",
                true
        );


        // Agregar tareas
        planificacion.agregarTarea(new Tarea("Definir requisitos", 1, true));
        planificacion.agregarTarea(new Tarea("Crear cronograma", 1, true));

        desarrollo.agregarActividad(planificacion);

        // El monitoreo comenzará automáticamente y verificará:
        // 1. El estado de las tareas cada minuto
        // 2. Si las tareas están próximas a vencer (2 horas o menos)
        // 3. Si las tareas han vencido
        // 4. Si las actividades están en riesgo debido a tareas vencidas

        // Para mantener el programa en ejecución
        try {
            Thread.sleep(TimeUnit.HOURS.toMillis(1)); // Ejecutar por 1 hora
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            MonitorProcesos.getInstance().detenerMonitoreo();
        }
    }
}