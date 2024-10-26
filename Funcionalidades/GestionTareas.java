package Proyecto.Funcionalidades;

import Proyecto.EstructurasDatos.Cola;
import Proyecto.EstructurasDatos.Nodo;
import Proyecto.ModelosBase.Actividad;
import Proyecto.ModelosBase.Proceso;
import Proyecto.ModelosBase.Tarea;
import java.util.UUID;

public class GestionTareas {
    private GestionProcesos gestionProcesos;

    public GestionTareas(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
    }

    public void agregarTarea(UUID procesoId, String nombreActividad, String descripcion, int duracion, boolean obligatoria) {
        Tarea nuevaTarea = new Tarea(descripcion, duracion, obligatoria);
        for (int i = 0; i < gestionProcesos.buscarProceso(procesoId).getActividades().getTamanio(); i++) {
            Actividad actividad = gestionProcesos.buscarProceso(procesoId).getActividades().getElementoEnPosicion(i);
            if (actividad.getNombre().equals(nombreActividad)) {
                validarTareasOpcionales(actividad.getTareas());
                actividad.agregarTarea(nuevaTarea);
            }
        }
    }

    public void insertarTareaEnPosicion(UUID procesoId, String nombreActividad, int posicion, String descripcion, int duracion, boolean obligatoria) {
        if (posicion < 0) {
            System.out.println("Posición inválida.");
            return;
        }
        Tarea nuevaTarea = new Tarea(descripcion, duracion, obligatoria);
        for (int i = 0; i < gestionProcesos.buscarProceso(procesoId).getActividades().getTamanio(); i++) {
            Actividad actividad = gestionProcesos.buscarProceso(procesoId).getActividades().getElementoEnPosicion(i);
            if (actividad.getNombre().equals(nombreActividad)) {
                Cola<Tarea> tareas = actividad.getTareas();
                Nodo<Tarea> tarea = tareas.getNodoPrimero();
                for (int j = 0; j < posicion && tarea != null; j++) {
                    tarea = tarea.getSiguienteNodo();
                }
                if (tarea != null) {
                    validarTareasOpcionales(tareas);
                    tareas.insertarDespuesDe(tarea.getValorNodo(), nuevaTarea);
                } else {
                    System.out.println("Posición fuera de rango.");
                }
            }
        }
    }

    private void validarTareasOpcionales(Cola<Tarea> tareas) {
        if (tareas == null || tareas.estaVacia()) {
            return;
        }

        Nodo<Tarea> actual = tareas.getNodoPrimero();
        int consecutivasOpcionales = 0;

        while (actual != null) {
            if (!actual.getValorNodo().isObligatoria()) {
                consecutivasOpcionales++;
                if (consecutivasOpcionales > 1) {
                    throw new IllegalStateException("No se permiten tareas opcionales consecutivas en la actividad.");
                }
            } else {
                consecutivasOpcionales = 0;
            }
            actual = actual.getSiguienteNodo();
        }
    }
}