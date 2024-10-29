package Proyecto.Funcionalidades;

import Proyecto.EstructurasDatos.Cola;
import Proyecto.EstructurasDatos.ListaEnlazada;
import Proyecto.EstructurasDatos.Nodo;
import Proyecto.ModelosBase.Actividad;
import Proyecto.ModelosBase.Proceso;
import Proyecto.ModelosBase.Tarea;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GestionConsultas {
    private final GestionProcesos gestionProcesos;

    public GestionConsultas(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
    }

    public void intercambiarActividades(UUID procesoId, String nombreActividad1, String nombreActividad2, boolean intercambiarTareas) {
        Proceso proceso = gestionProcesos.buscarProceso(procesoId);
        if (proceso == null) {
            throw new IllegalStateException("Proceso no encontrado.");
        }

        ListaEnlazada<Actividad> actividades = proceso.getActividades();
        Actividad actividad1 = buscarActividadPorNombre(actividades, nombreActividad1);
        Actividad actividad2 = buscarActividadPorNombre(actividades, nombreActividad2);

        if (actividad1 == null || actividad2 == null) {
            throw new IllegalArgumentException("Una o ambas actividades no fueron encontradas.");
        }

        // Almacenar las tareas originales
        Cola<Tarea> tareasActividad1 = new Cola<>();
        Cola<Tarea> tareasActividad2 = new Cola<>();

        // Copiar las tareas originales
        copiarTareas(actividad1.getTareas(), tareasActividad1);
        copiarTareas(actividad2.getTareas(), tareasActividad2);

        // Realizar el intercambio de posiciones en la lista enlazada
        intercambiarPosicionesActividades(actividades, actividad1, actividad2);

        // Si no se deben intercambiar las tareas, restaurar las tareas originales
        if (!intercambiarTareas) {
            actividad1.setTareas(tareasActividad2);
            actividad2.setTareas(tareasActividad1);
        }
    }

    private void copiarTareas(Cola<Tarea> origen, Cola<Tarea> destino) {
        Nodo<Tarea> actual = origen.getNodoPrimero();
        while (actual != null) {
            destino.encolar(actual.getValorNodo());
            actual = actual.getSiguienteNodo();
        }
    }

    private void intercambiarPosicionesActividades(ListaEnlazada<Actividad> actividades,
                                                   Actividad actividad1,
                                                   Actividad actividad2) {
        Nodo<Actividad> nodoActual = actividades.getCabeza();
        Nodo<Actividad> nodoAnterior1 = null;
        Nodo<Actividad> nodo1 = null;
        Nodo<Actividad> nodoAnterior2 = null;
        Nodo<Actividad> nodo2 = null;
        Nodo<Actividad> nodoAnterior = null;

        // Encontrar los nodos y sus anteriores
        while (nodoActual != null) {
            if (nodoActual.getValorNodo().equals(actividad1)) {
                nodoAnterior1 = nodoAnterior;
                nodo1 = nodoActual;
            }
            if (nodoActual.getValorNodo().equals(actividad2)) {
                nodoAnterior2 = nodoAnterior;
                nodo2 = nodoActual;
            }
            nodoAnterior = nodoActual;
            nodoActual = nodoActual.getSiguienteNodo();
        }

        if (nodo1 != null && nodo2 != null) {
            // Guardar referencias temporales
            Nodo<Actividad> siguiente1 = nodo1.getSiguienteNodo();
            Nodo<Actividad> siguiente2 = nodo2.getSiguienteNodo();

            // Caso especial: nodos adyacentes
            if (nodo1.getSiguienteNodo() == nodo2) {
                nodo1.setSiguienteNodo(siguiente2);
                nodo2.setSiguienteNodo(nodo1);
                if (nodoAnterior1 != null) {
                    nodoAnterior1.setSiguienteNodo(nodo2);
                } else {
                    actividades.setCabeza(nodo2);
                }
            } else if (nodo2.getSiguienteNodo() == nodo1) {
                nodo2.setSiguienteNodo(siguiente1);
                nodo1.setSiguienteNodo(nodo2);
                if (nodoAnterior2 != null) {
                    nodoAnterior2.setSiguienteNodo(nodo1);
                } else {
                    actividades.setCabeza(nodo1);
                }
            } else {
                // Caso general: nodos no adyacentes
                nodo1.setSiguienteNodo(siguiente2);
                nodo2.setSiguienteNodo(siguiente1);

                if (nodoAnterior1 != null) {
                    nodoAnterior1.setSiguienteNodo(nodo2);
                } else {
                    actividades.setCabeza(nodo2);
                }

                if (nodoAnterior2 != null) {
                    nodoAnterior2.setSiguienteNodo(nodo1);
                } else {
                    actividades.setCabeza(nodo1);
                }
            }
        }
    }

    public List<Tarea> buscarTareas(UUID procesoId, TipoBusqueda tipoBusqueda, String criterio) {
        Proceso proceso = gestionProcesos.buscarProceso(procesoId);
        if (proceso == null) {
            throw new IllegalStateException("Proceso no encontrado.");
        }

        List<Tarea> tareasEncontradas = new ArrayList<>();
        ListaEnlazada<Actividad> actividades = proceso.getActividades();

        switch (tipoBusqueda) {
            case DESDE_INICIO:
                buscarDesdeInicio(actividades, criterio, tareasEncontradas);
                break;
            case DESDE_ACTIVIDAD_ACTUAL:
                Actividad actividadActual = encontrarUltimaActividad(actividades);
                if (actividadActual != null) {
                    buscarDesdeActividad(actividadActual, criterio, tareasEncontradas);
                }
                break;
            case DESDE_ACTIVIDAD_ESPECIFICA:
                Actividad actividadEspecifica = buscarActividadPorNombre(actividades, criterio);
                if (actividadEspecifica != null) {
                    buscarDesdeActividad(actividadEspecifica, criterio, tareasEncontradas);
                }
                break;
        }

        return tareasEncontradas;
    }

    public TiempoProceso calcularTiempoProceso(UUID procesoId) {
        Proceso proceso = gestionProcesos.buscarProceso(procesoId);
        if (proceso == null) {
            throw new IllegalStateException("Proceso no encontrado.");
        }

        int tiempoMinimo = 0;
        int tiempoMaximo = 0;

        Nodo<Actividad> actualActividad = proceso.getActividades().getCabeza();
        while (actualActividad != null) {
            Actividad actividad = actualActividad.getValorNodo();
            Cola<Tarea> tareas = actividad.getTareas();
            Nodo<Tarea> actualTarea = tareas.getNodoPrimero();

            while (actualTarea != null) {
                Tarea tarea = actualTarea.getValorNodo();

                if (actividad.isObligatoria() && tarea.isObligatoria()) {
                    tiempoMinimo += tarea.getDuracion();
                }

                tiempoMaximo += tarea.getDuracion();

                actualTarea = actualTarea.getSiguienteNodo();
            }

            actualActividad = actualActividad.getSiguienteNodo();
        }

        LocalDateTime ahora = LocalDateTime.now();
        Duration tiempoTranscurrido = Duration.between(proceso.getFechaInicio(), ahora);
        long minutosTranscurridos = tiempoTranscurrido.toMinutes();

        int tiempoMinimoRestante = Math.max(tiempoMinimo - (int)minutosTranscurridos, 0);
        int tiempoMaximoRestante = Math.max(tiempoMaximo - (int)minutosTranscurridos, 0);

        return new TiempoProceso(tiempoMinimoRestante, tiempoMaximoRestante);
    }

    private void buscarDesdeInicio(ListaEnlazada<Actividad> actividades, String criterio, List<Tarea> tareasEncontradas) {
        Nodo<Actividad> actual = actividades.getCabeza();
        while (actual != null) {
            buscarEnActividad(actual.getValorNodo(), criterio, tareasEncontradas);
            actual = actual.getSiguienteNodo();
        }
    }

    private void buscarDesdeActividad(Actividad actividad, String criterio, List<Tarea> tareasEncontradas) {
        Actividad actual = actividad;
        while (actual != null) {
            // Agregar todas las tareas de la actividad sin filtrar por descripción
            Cola<Tarea> tareas = actual.getTareas();
            Nodo<Tarea> nodoTarea = tareas.getNodoPrimero();
            while (nodoTarea != null) {
                tareasEncontradas.add(nodoTarea.getValorNodo());
                nodoTarea = nodoTarea.getSiguienteNodo();
            }
            actual = actual.getSiguiente();
        }
    }

    private void buscarEnActividad(Actividad actividad, String criterio, List<Tarea> tareasEncontradas) {
        Cola<Tarea> tareas = actividad.getTareas();
        Nodo<Tarea> actual = tareas.getNodoPrimero();

        while (actual != null) {
            Tarea tarea = actual.getValorNodo();
            // Solo aplicar el filtro por descripción si hay un criterio
            if (criterio.isEmpty() || tarea.getDescripcion().toLowerCase().contains(criterio.toLowerCase())) {
                tareasEncontradas.add(tarea);
            }
            actual = actual.getSiguienteNodo();
        }
    }

    private Actividad encontrarUltimaActividad(ListaEnlazada<Actividad> actividades) {
        if (actividades.getCabeza() == null) {
            return null;
        }

        Nodo<Actividad> actual = actividades.getCabeza();
        while (actual.getSiguienteNodo() != null) {
            actual = actual.getSiguienteNodo();
        }
        return actual.getValorNodo();
    }

    private Actividad buscarActividadPorNombre(ListaEnlazada<Actividad> actividades, String nombre) {
        Nodo<Actividad> actual = actividades.getCabeza();
        while (actual != null) {
            if (actual.getValorNodo().getNombre().equals(nombre)) {
                return actual.getValorNodo();
            }
            actual = actual.getSiguienteNodo();
        }
        return null;
    }

    public static class TiempoProceso {
        private final int tiempoMinimo;
        private final int tiempoMaximo;

        public TiempoProceso(int tiempoMinimo, int tiempoMaximo) {
            this.tiempoMinimo = tiempoMinimo;
            this.tiempoMaximo = tiempoMaximo;
        }

        public int getTiempoMinimo() {
            return tiempoMinimo;
        }

        public int getTiempoMaximo() {
            return tiempoMaximo;
        }
    }

    public enum TipoBusqueda {
        DESDE_INICIO,
        DESDE_ACTIVIDAD_ACTUAL,
        DESDE_ACTIVIDAD_ESPECIFICA
    }

    private static class TiempoActividad {
        private final int tiempoMinimo;
        private final int tiempoMaximo;

        public TiempoActividad(int tiempoMinimo, int tiempoMaximo) {
            this.tiempoMinimo = tiempoMinimo;
            this.tiempoMaximo = tiempoMaximo;
        }
    }
}