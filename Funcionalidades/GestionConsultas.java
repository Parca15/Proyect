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
                // Asumimos que la actividad actual es la última agregada
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

        Nodo<Actividad> actual = proceso.getActividades().getCabeza();
        while (actual != null) {
            Actividad actividad = actual.getValorNodo();
            TiempoActividad tiempoActividad = calcularTiempoActividad(actividad);

            if (actividad.isObligatoria()) {
                tiempoMinimo += tiempoActividad.tiempoMinimo;
            }
            tiempoMaximo += tiempoActividad.tiempoMaximo;

            actual = actual.getSiguienteNodo();
        }

        // Calcula el tiempo restante
        LocalDateTime fechaInicio = proceso.getFechaInicio();
        Duration duracionTranscurrida = Duration.between(fechaInicio, LocalDateTime.now());
        long minutosTranscurridos = duracionTranscurrida.toMinutes();
        long minutosRestantes = tiempoMaximo - minutosTranscurridos;

        return new TiempoProceso(tiempoMinimo, Math.max((int) minutosRestantes, 0));
    }

    private TiempoActividad calcularTiempoActividad(Actividad actividad) {
        int tiempoMinimo = 0;
        int tiempoMaximo = 0;

        Cola<Tarea> tareas = actividad.getTareas();
        Nodo<Tarea> actual = tareas.getNodoPrimero();

        while (actual != null) {
            Tarea tarea = actual.getValorNodo();
            if (tarea.isObligatoria()) {
                tiempoMinimo += tarea.getDuracion();
                tiempoMaximo += tarea.getDuracion();
            } else {
                tiempoMaximo += tarea.getDuracion();
            }
            actual = actual.getSiguienteNodo();
        }

        return new TiempoActividad(tiempoMinimo, tiempoMaximo);
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
            buscarEnActividad(actual, criterio, tareasEncontradas);
            actual = actual.getSiguiente();
        }
    }

    private void buscarEnActividad(Actividad actividad, String criterio, List<Tarea> tareasEncontradas) {
        Cola<Tarea> tareas = actividad.getTareas();
        Nodo<Tarea> actual = tareas.getNodoPrimero();

        while (actual != null) {
            Tarea tarea = actual.getValorNodo();
            if (tarea.getDescripcion().toLowerCase().contains(criterio.toLowerCase())) {
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

    public Object buscarTareasPorActividad(UUID id, String nombre) {
        return null;
    }

    public enum TipoBusqueda {
        DESDE_INICIO,
        DESDE_ACTIVIDAD_ACTUAL,
        DESDE_ACTIVIDAD_ESPECIFICA
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

    private static class TiempoActividad {
        private final int tiempoMinimo;
        private final int tiempoMaximo;

        public TiempoActividad(int tiempoMinimo, int tiempoMaximo) {
            this.tiempoMinimo = tiempoMinimo;
            this.tiempoMaximo = tiempoMaximo;
        }
    }
}