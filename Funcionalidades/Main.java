package Proyecto.Funcionalidades;

import Proyecto.ModelosBase.Actividad;
import Proyecto.ModelosBase.Proceso;
import Proyecto.ModelosBase.Tarea;

import java.util.HashMap;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        // Crear instancia de GestionProcesos con un mapa vacío de procesos
        GestionProcesos gestionProcesos = new GestionProcesos(new HashMap<>());

        // Crear dos procesos
        Proceso proceso1 = gestionProcesos.crearProceso("Proceso 1");
        Proceso proceso2 = gestionProcesos.crearProceso("Proceso 2");

        // Crear instancia de GestionTareas
        GestionTareas gestionTareas = new GestionTareas(gestionProcesos);

        // Añadir actividades y tareas a cada proceso
        for (int i = 1; i <= 2; i++) {
            Proceso procesoActual = (i == 1) ? proceso1 : proceso2;
            UUID procesoId = procesoActual.getId();

            for (int j = 1; j <= 4; j++) {
                // Crear y agregar actividad
                String nombreActividad = "Actividad " + j;
                Actividad actividad = new Actividad(nombreActividad, "Descripción de " + nombreActividad, true);
                procesoActual.agregarActividad(actividad);

                // Agregar dos tareas a cada actividad
                gestionTareas.agregarTarea(procesoId, nombreActividad, "Tarea Obligatoria " + j, 60 + j * 10, true);
                gestionTareas.agregarTarea(procesoId, nombreActividad, "Tarea Opcional " + j, 30 + j * 5, false);
            }
        }

        // Imprimir detalles de los procesos, actividades y tareas
        imprimirDetallesProceso(proceso1);
        imprimirDetallesProceso(proceso2);
    }

    private static void imprimirDetallesProceso(Proceso proceso) {
        System.out.println("\nDetalles de " + proceso.getNombre() + " (ID: " + proceso.getId() + ")");
        for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
            Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
            System.out.println("  - Actividad: " + actividad.getNombre());
            for (int j = 0; j < actividad.getTareas().obtenerTamanio(); j++) {
                Tarea tarea = actividad.getTareas().obtenerNodoPosicion(j).getValorNodo();
                System.out.println("    - Tarea: " + tarea.getDescripcion() + " (Duración: " + tarea.getDuracion() + " min, Obligatoria: " + tarea.isObligatoria() + ")");
            }
        }
    }
}
