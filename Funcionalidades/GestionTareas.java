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
        Proceso proceso = gestionProcesos.buscarProceso(procesoId);
        if (proceso == null) {
            throw new IllegalStateException("Proceso no encontrado.");
        }

        for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
            Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
            if (actividad.getNombre().equals(nombreActividad)) {
                Cola<Tarea> tareas = actividad.getTareas();
                Tarea nuevaTarea = new Tarea(descripcion, duracion, obligatoria);

                // Validar si es posible agregar la tarea al final
                if (!validarInsercionTarea(tareas, nuevaTarea, -1)) {
                    throw new IllegalStateException("No se pueden tener dos tareas opcionales consecutivas.");
                }

                actividad.agregarTarea(nuevaTarea);
                return;
            }
        }
        throw new IllegalStateException("Actividad no encontrada.");
    }

    public void insertarTareaEnPosicion(UUID procesoId, String nombreActividad, int posicion, String descripcion, int duracion, boolean obligatoria) {
        if (posicion < 0) {
            throw new IllegalStateException("La posición no puede ser negativa.");
        }

        Proceso proceso = gestionProcesos.buscarProceso(procesoId);
        if (proceso == null) {
            throw new IllegalStateException("Proceso no encontrado.");
        }

        for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
            Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
            if (actividad.getNombre().equals(nombreActividad)) {
                Cola<Tarea> tareas = actividad.getTareas();
                Tarea nuevaTarea = new Tarea(descripcion, duracion, obligatoria);

                // Validar si es posible insertar la tarea en la posición especificada
                if (!validarInsercionTarea(tareas, nuevaTarea, posicion)) {
                    throw new IllegalStateException("No se pueden tener dos tareas opcionales consecutivas.");
                }

                // Si la cola está vacía o queremos insertar al inicio
                if (tareas.estaVacia() || posicion == 0) {
                    tareas.insertarAlInicio(nuevaTarea);
                    return;
                }

                // Obtener el tamaño actual de la cola
                int tamano = obtenerTamano(tareas);

                // Si queremos insertar al final
                if (posicion >= tamano) {
                    tareas.insertarAlFinal(nuevaTarea);
                    return;
                }

                // Insertar en una posición específica
                Nodo<Tarea> actual = tareas.getNodoPrimero();
                for (int j = 0; j < posicion - 1; j++) {
                    actual = actual.getSiguienteNodo();
                }

                tareas.insertarDespuesDe(actual.getValorNodo(), nuevaTarea);
                return;
            }
        }
        throw new IllegalStateException("Actividad no encontrada.");
    }

    private boolean validarInsercionTarea(Cola<Tarea> tareas, Tarea nuevaTarea, int posicion) {
        if (tareas.estaVacia()) {
            return true;
        }

        // Convertir la cola a un arreglo para facilitar la validación
        Tarea[] tareasArray = convertirColaAArreglo(tareas);

        // Si la posición es -1, significa que se agregará al final
        if (posicion == -1) {
            posicion = tareasArray.length;
        }

        // Validar si la nueva tarea es opcional
        if (!nuevaTarea.isObligatoria()) {
            // Verificar tarea anterior si no es la primera posición
            if (posicion > 0 && !tareasArray[posicion - 1].isObligatoria()) {
                return false;
            }

            // Verificar tarea siguiente si no es la última posición
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