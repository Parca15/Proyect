package Controller;

import Funcionalidades.GestionConsultas;
import Funcionalidades.GestionTareas;
import Interfaz.ProcesoPanel;
import Interfaz.TareaPanel;
import ModelosBase.Actividad;
import ModelosBase.Proceso;
import EstructurasDatos.ListaEnlazada;
import EstructurasDatos.Nodo;

import java.util.UUID;

public class TareaController {

    private final GestionTareas gestionTareas;
    private final GestionConsultas gestionConsultas;
    private final ProcesoPanel procesoPanel;
    private TareaPanel view;
    private UUID lastSelectedProcesoId;

    public TareaController(GestionTareas gestionTareas, GestionConsultas gestionConsultas, ProcesoPanel procesoPanel) {
        this.gestionTareas = gestionTareas;
        this.gestionConsultas = gestionConsultas;
        this.procesoPanel = procesoPanel;
    }

    public void setView(TareaPanel view) {
        this.view = view;
    }

    public void actualizarComboBoxActividades() {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            view.limpiarComboBoxes();
            return;
        }

        Proceso proceso = procesoPanel.getGestionProcesos().buscarProceso(procesoId);
        System.out.println("Actualizando actividades para proceso: " + procesoId);

        if (proceso != null) {
            ListaEnlazada<Actividad> actividades = proceso.getActividades();

            if (actividades != null && actividades.getCabeza() != null) {
                lastSelectedProcesoId = procesoId;
                view.limpiarComboBoxes();

                Nodo<Actividad> actual = actividades.getCabeza();
                while (actual != null) {
                    String nombreActividad = actual.getValorNodo().getNombre();
                    System.out.println("Agregando actividad: " + nombreActividad);
                    view.agregarActividad(nombreActividad);
                    actual = actual.getSiguienteNodo();
                }

                view.seleccionarPrimeraActividad();
            } else {
                System.out.println("No se encontraron actividades para el proceso");
            }
        } else {
            System.out.println("No se encontró el proceso con ID: " + procesoId);
        }
    }

    public void crearTarea(String actividad, String descripcion, int duracion, boolean obligatoria, int tipoInsercion, int posicion) {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            throw new IllegalStateException("Por favor seleccione un proceso primero");
        }

        if (actividad == null) {
            throw new IllegalStateException("Por favor seleccione una actividad");
        }

        if (descripcion.trim().isEmpty()) {
            throw new IllegalStateException("Por favor complete la descripción");
        }

        if (tipoInsercion == 0) {
            gestionTareas.agregarTarea(procesoId, actividad, descripcion, duracion, obligatoria);
        } else {
            gestionTareas.insertarTareaEnPosicion(procesoId, actividad, posicion - 1, descripcion, duracion, obligatoria);
        }
    }

    public String buscarTareas(int tipoBusqueda, String actividadSeleccionada) {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            throw new IllegalStateException("Por favor seleccione un proceso primero");
        }

        GestionConsultas.TipoBusqueda tipo;
        String criterio = "";

        switch (tipoBusqueda) {
            case 0:
                tipo = GestionConsultas.TipoBusqueda.DESDE_INICIO;
                break;
            case 1:
                tipo = GestionConsultas.TipoBusqueda.DESDE_ACTIVIDAD_ACTUAL;
                break;
            case 2:
                tipo = GestionConsultas.TipoBusqueda.DESDE_ACTIVIDAD_ESPECIFICA;
                criterio = actividadSeleccionada;
                if (criterio == null) {
                    throw new IllegalStateException("Por favor seleccione una actividad para la búsqueda");
                }
                break;
            default:
                throw new IllegalStateException("Tipo de búsqueda no válido");
        }

        var tareas = gestionConsultas.buscarTareas(procesoId, tipo, criterio);
        StringBuilder resultado = new StringBuilder();
        for (var tarea : tareas) {
            resultado.append("Descripción: ").append(tarea.getDescripcion())
                    .append("\nDuración: ").append(tarea.getDuracion())
                    .append(" min\nObligatoria: ").append(tarea.isObligatoria())
                    .append("\n\n");
        }
        return resultado.toString();
    }

    public GestionConsultas.TiempoProceso calcularDuracionProceso() {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            throw new IllegalStateException("Por favor seleccione un proceso primero");
        }
        return gestionConsultas.calcularTiempoProceso(procesoId);
    }
}