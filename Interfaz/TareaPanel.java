package Proyecto.Interfaz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.UUID;

import Proyecto.Funcionalidades.GestionConsultas;
import Proyecto.Funcionalidades.GestionTareas;
import Proyecto.ModelosBase.Actividad;
import Proyecto.ModelosBase.Proceso;
import Proyecto.EstructurasDatos.ListaEnlazada;
import Proyecto.EstructurasDatos.Nodo;

public class TareaPanel extends JPanel {
    private final GestionTareas gestionTareas;
    private final GestionConsultas gestionConsultas;
    private final ProcesoPanel procesoPanel;
    private final JTextField descripcionField;
    private final JSpinner duracionSpinner;
    private final JCheckBox obligatoriaCheck;
    private final JComboBox<String> actividadCombo;
    private final JComboBox<String> tipoInsercionCombo;
    private final JSpinner posicionSpinner;
    private final JPanel busquedaPanel;
    private final JComboBox<String> tipoBusquedaCombo;
    private final JComboBox<String> actividadBusquedaCombo;
    private final JTextArea resultadosBusqueda;
    private UUID lastSelectedProcesoId; // Nueva variable para tracking

    public TareaPanel(GestionTareas gestionTareas, GestionConsultas gestionConsultas, ProcesoPanel procesoPanel) {
        this.gestionTareas = gestionTareas;
        this.gestionConsultas = gestionConsultas;
        this.procesoPanel = procesoPanel;
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Definición de los nuevos colores siguiendo el tema morado
        Color colorPrincipal = new Color(111, 63, 182);      // Morado principal
        Color colorSecundario = new Color(138, 43, 226);        // Morado más claro
        Color colorAccent = new Color(123, 104, 238);          // Morado claro/lavanda
        Color colorFondo = new Color(147, 112, 219, 100);    // Morado claro semi transparente
        Color colorTexto = Color.black;                      // Texto blanco

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(colorSecundario);
        tabbedPane.setForeground(colorTexto);

        // === Panel de Creación de Tareas ===
        JPanel creacionPanel = new JPanel(new GridBagLayout());
        creacionPanel.setBackground(new Color(111, 63, 182, 100));  // Morado semi transparente
        creacionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(colorAccent, 2),
                        "Crear Nueva Tarea"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        descripcionField = new JTextField(20);
        descripcionField.setBorder(BorderFactory.createLineBorder(colorAccent));
        descripcionField.setBackground(new Color(255, 255, 255, 220));

        duracionSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 999, 1));
        duracionSpinner.getEditor().getComponent(0).setBackground(new Color(255, 255, 255, 220));

        obligatoriaCheck = new JCheckBox("Obligatoria");
        obligatoriaCheck.setForeground(colorTexto);
        obligatoriaCheck.setBackground(new Color(0, 0, 0, 0));

        actividadCombo = new JComboBox<>();
        actividadCombo.setBackground(new Color(255, 255, 255, 220));
        actividadCombo.setBorder(BorderFactory.createLineBorder(colorAccent));

        String[] tiposInsercion = {"Al final", "En posición específica"};
        tipoInsercionCombo = new JComboBox<>(tiposInsercion);
        tipoInsercionCombo.setBackground(new Color(255, 255, 255, 220));

        posicionSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        posicionSpinner.setEnabled(false);
        posicionSpinner.getEditor().getComponent(0).setBackground(new Color(255, 255, 255, 220));

        // Agregar componentes al panel de creación
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblActividad = new JLabel("Actividad:");
        lblActividad.setForeground(colorTexto);
        creacionPanel.add(lblActividad, gbc);
        gbc.gridx = 1;
        creacionPanel.add(actividadCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setForeground(colorTexto);
        creacionPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        creacionPanel.add(descripcionField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDuracion = new JLabel("Duración (min):");
        lblDuracion.setForeground(colorTexto);
        creacionPanel.add(lblDuracion, gbc);
        gbc.gridx = 1;
        creacionPanel.add(duracionSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        creacionPanel.add(obligatoriaCheck, gbc);

        gbc.gridy = 4;
        JLabel lblTipoInsercion = new JLabel("Tipo de inserción:");
        lblTipoInsercion.setForeground(colorTexto);
        creacionPanel.add(lblTipoInsercion, gbc);
        gbc.gridy = 5;
        creacionPanel.add(tipoInsercionCombo, gbc);

        gbc.gridy = 6;
        JPanel posicionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        posicionPanel.setBackground(new Color(0, 0, 0, 0));
        JLabel lblPosicion = new JLabel("Posición:");
        lblPosicion.setForeground(colorTexto);
        posicionPanel.add(lblPosicion);
        posicionPanel.add(posicionSpinner);
        creacionPanel.add(posicionPanel, gbc);

        JButton crearButton = new JButton("Crear Tarea");
        crearButton.setBackground(colorAccent);
        crearButton.setForeground(colorTexto);
        crearButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        crearButton.setFocusPainted(false);
        gbc.gridy = 7;
        creacionPanel.add(crearButton, gbc);

        // === Panel de Búsqueda de Tareas ===
        busquedaPanel = new JPanel(new GridBagLayout());
        busquedaPanel.setBackground(new Color(111, 63, 182, 100));
        busquedaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(colorAccent, 2),
                        "Buscar Tareas"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        String[] tiposBusqueda = {
                "Desde inicio del proceso",
                "Desde actividad actual",
                "Desde actividad específica"
        };
        tipoBusquedaCombo = new JComboBox<>(tiposBusqueda);
        tipoBusquedaCombo.setBackground(new Color(255, 255, 255, 220));

        actividadBusquedaCombo = new JComboBox<>();
        actividadBusquedaCombo.setBackground(new Color(255, 255, 255, 220));
        actividadBusquedaCombo.setEnabled(false);

        resultadosBusqueda = new JTextArea(10, 30);
        resultadosBusqueda.setBackground(new Color(255, 255, 255, 220));
        resultadosBusqueda.setBorder(BorderFactory.createLineBorder(colorAccent));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        busquedaPanel.add(tipoBusquedaCombo, gbc);

        gbc.gridy = 1;
        JLabel lblActividadInicio = new JLabel("Actividad de inicio:");
        lblActividadInicio.setForeground(colorTexto);
        busquedaPanel.add(lblActividadInicio, gbc);
        gbc.gridy = 2;
        busquedaPanel.add(actividadBusquedaCombo, gbc);

        gbc.gridy = 3;
        JButton buscarButton = new JButton("Buscar");
        buscarButton.setBackground(colorAccent);
        buscarButton.setForeground(colorTexto);
        buscarButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        buscarButton.setFocusPainted(false);
        busquedaPanel.add(buscarButton, gbc);

        gbc.gridy = 4;
        busquedaPanel.add(new JScrollPane(resultadosBusqueda), gbc);

        // === Panel de Consulta de Duración ===
        JPanel duracionPanel = new JPanel(new GridBagLayout());
        duracionPanel.setBackground(new Color(111, 63, 182, 100));
        duracionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(colorAccent, 2),
                        "Consultar Duración del Proceso"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JButton calcularButton = new JButton("Calcular Duración");
        calcularButton.setBackground(colorAccent);
        calcularButton.setForeground(colorTexto);
        calcularButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        calcularButton.setFocusPainted(false);

        JTextArea resultadoDuracion = new JTextArea(5, 30);
        resultadoDuracion.setEditable(false);
        resultadoDuracion.setBackground(new Color(255, 255, 255, 220));
        resultadoDuracion.setBorder(BorderFactory.createLineBorder(colorAccent));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        duracionPanel.add(calcularButton, gbc);
        gbc.gridy = 1;
        duracionPanel.add(new JScrollPane(resultadoDuracion), gbc);

        // Agregar los paneles al TabbedPane
        tabbedPane.addTab("Crear Tarea", creacionPanel);
        tabbedPane.addTab("Buscar Tareas", busquedaPanel);
        tabbedPane.addTab("Duración del Proceso", duracionPanel);

        this.add(tabbedPane, BorderLayout.CENTER);

        // === Eventos ===
        procesoPanel.addActividadListener(() -> {
            SwingUtilities.invokeLater(this::actualizarComboBoxActividades);
        });
        procesoPanel.addProcesoSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(this::actualizarComboBoxActividades);
            }
        });
        tipoInsercionCombo.addActionListener(e ->
                posicionSpinner.setEnabled(tipoInsercionCombo.getSelectedIndex() == 1));

        tipoBusquedaCombo.addActionListener(e -> {
            actividadBusquedaCombo.setEnabled(tipoBusquedaCombo.getSelectedIndex() == 2);
            if (tipoBusquedaCombo.getSelectedIndex() != 2) {
                actividadBusquedaCombo.setSelectedIndex(-1);
            }
        });

        crearButton.addActionListener(e -> {
            // Código para crear una nueva actividad
            crearTarea();
            // Actualizar los comboboxes después de crear la nueva actividad
            SwingUtilities.invokeLater(this::actualizarComboBoxActividades);
        });
        buscarButton.addActionListener(e -> buscarTareas());
        calcularButton.addActionListener(e -> {
            UUID procesoId = procesoPanel.getSelectedProcesoId();
            if (procesoId != null) {
                GestionConsultas.TiempoProceso tiempo = gestionConsultas.calcularTiempoProceso(procesoId);
                resultadoDuracion.setText(String.format(
                        "Duración mínima: %d minutos\nDuración máxima: %d minutos",
                        tiempo.getTiempoMinimo(),
                        tiempo.getTiempoMaximo()
                ));
            } else {
                mostrarError("Por favor seleccione un proceso primero");
            }
        });

    }

   private void actualizarComboBoxActividades() {
       // Limpiar los combobox
       SwingUtilities.invokeLater(() -> {
           actividadCombo.removeAllItems();
           actividadBusquedaCombo.removeAllItems();
       });

       UUID procesoId = procesoPanel.getSelectedProcesoId();
       if (procesoId == null) {
           return;
       }

       // Obtener el proceso seleccionado directamente
       Proceso proceso = procesoPanel.getGestionProcesos().buscarProceso(procesoId);
       System.out.println("Actualizando actividades para proceso: " + procesoId); // Debug

       if (proceso != null) {
           ListaEnlazada<Actividad> actividades = proceso.getActividades();

           if (actividades != null && actividades.getCabeza() != null) {
               lastSelectedProcesoId = procesoId;

               // Usar SwingUtilities.invokeLater para asegurar que las actualizaciones de UI
               // se realizan en el EDT (Event Dispatch Thread)
               SwingUtilities.invokeLater(() -> {
                   Nodo<Actividad> actual = actividades.getCabeza();
                   while (actual != null) {
                       String nombreActividad = actual.getValorNodo().getNombre();
                       System.out.println("Agregando actividad: " + nombreActividad); // Debug
                       actividadCombo.addItem(nombreActividad);
                       actividadBusquedaCombo.addItem(nombreActividad);
                       actual = actual.getSiguienteNodo();
                   }

                   // Seleccionar el primer item si hay items disponibles
                   if (actividadCombo.getItemCount() > 0) {
                       actividadCombo.setSelectedIndex(0);
                       actividadBusquedaCombo.setSelectedIndex(0);
                   } else {
                       actividadCombo.setSelectedIndex(-1);
                       actividadBusquedaCombo.setSelectedIndex(-1);
                   }
               });
           } else {
               System.out.println("No se encontraron actividades para el proceso"); // Debug
           }
       } else {
           System.out.println("No se encontró el proceso con ID: " + procesoId); // Debug
       }
   }

    // Método adicional para forzar la actualización manual si es necesario
    public void forzarActualizacionActividades() {
        SwingUtilities.invokeLater(this::actualizarComboBoxActividades);
    }

    private void crearTarea() {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            mostrarError("Por favor seleccione un proceso primero");
            return;
        }

        String actividad = (String) actividadCombo.getSelectedItem();
        if (actividad == null) {
            mostrarError("Por favor seleccione una actividad");
            return;
        }

        String descripcion = descripcionField.getText().trim();
        int duracion = (Integer) duracionSpinner.getValue();
        boolean obligatoria = obligatoriaCheck.isSelected();

        if (descripcion.isEmpty()) {
            mostrarError("Por favor complete la descripción");
            return;
        }

        try {
            if (tipoInsercionCombo.getSelectedIndex() == 0) {
                gestionTareas.agregarTarea(procesoId, actividad, descripcion, duracion, obligatoria);
            } else {
                int posicion = (Integer) posicionSpinner.getValue() - 1;
                gestionTareas.insertarTareaEnPosicion(procesoId, actividad, posicion, descripcion, duracion, obligatoria);
            }
            limpiarCampos();
            mostrarExito("Tarea creada exitosamente");
        } catch (Exception ex) {
            mostrarError("Error al crear la tarea: " + ex.getMessage());
        }
    }

    private void buscarTareas() {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            mostrarError("Por favor seleccione un proceso primero");
            return;
        }

        try {
            GestionConsultas.TipoBusqueda tipoBusqueda;
            String criterio = "";

            switch (tipoBusquedaCombo.getSelectedIndex()) {
                case 0:
                    tipoBusqueda = GestionConsultas.TipoBusqueda.DESDE_INICIO;
                    break;
                case 1:
                    tipoBusqueda = GestionConsultas.TipoBusqueda.DESDE_ACTIVIDAD_ACTUAL;
                    break;
                case 2:
                    tipoBusqueda = GestionConsultas.TipoBusqueda.DESDE_ACTIVIDAD_ESPECIFICA;
                    criterio = (String) actividadBusquedaCombo.getSelectedItem();
                    if (criterio == null) {
                        mostrarError("Por favor seleccione una actividad para la búsqueda");
                        return;
                    }
                    break;
                default:
                    return;
            }

            var tareas = gestionConsultas.buscarTareas(procesoId, tipoBusqueda, criterio);
            StringBuilder resultado = new StringBuilder();
            for (var tarea : tareas) {
                resultado.append("Descripción: ").append(tarea.getDescripcion())
                        .append("\nDuración: ").append(tarea.getDuracion())
                        .append(" min\nObligatoria: ").append(tarea.isObligatoria())
                        .append("\n\n");
            }
            resultadosBusqueda.setText(resultado.toString());

        } catch (Exception ex) {
            mostrarError("Error al buscar tareas: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        descripcionField.setText("");
        duracionSpinner.setValue(30);
        obligatoriaCheck.setSelected(false);
        tipoInsercionCombo.setSelectedIndex(0);
        posicionSpinner.setValue(1);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}