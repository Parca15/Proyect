package Interfaz;


import Funcionalidades.GestionActividades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class ActividadPanel extends JPanel {
    private final GestionActividades gestionActividades;
    private final ProcesoPanel procesoPanel;
    private final JTextField nombreField;
    private final JTextArea descripcionArea;
    private final JCheckBox obligatoriaCheck;
    private final JComboBox<String> tipoInsercionCombo;
    private final JTextField actividadPrecedenteField;

    public ActividadPanel(GestionActividades gestionActividades, ProcesoPanel procesoPanel) {
        this.gestionActividades = gestionActividades;
        this.procesoPanel = procesoPanel;
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Definición de los nuevos colores para coincidir con el tema morado
        Color colorFondoPrincipal = new Color(147, 112, 219);    // Morado principal
        Color colorSecundario = new Color(138, 43, 226);        // Morado más claro
        Color colorAccent = new Color(123, 104, 238);          // Morado claro/lavanda
        Color colorTexto = Color.black;                        // Texto blanco

        // Panel para datos de la actividad
        JPanel datosPanel = new JPanel(new GridBagLayout());
        datosPanel.setBackground(new Color(colorFondoPrincipal.getRed(),
                colorFondoPrincipal.getGreen(),
                colorFondoPrincipal.getBlue(), 180));

        // Crear TitledBorder directamente
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorAccent, 2),
                "Datos de la Actividad"
        );
        titledBorder.setTitleColor(colorTexto);

        // Establecer el borde
        datosPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de entrada con estilos
        nombreField = new JTextField(20);
        styleTextField(nombreField, colorSecundario);

        descripcionArea = new JTextArea(3, 20);
        descripcionArea.setLineWrap(true);
        styleTextArea(descripcionArea, colorSecundario);
        JScrollPane scrollDescripcion = new JScrollPane(descripcionArea);
        scrollDescripcion.setBorder(BorderFactory.createLineBorder(colorSecundario));

        obligatoriaCheck = new JCheckBox("Obligatoria");
        obligatoriaCheck.setForeground(colorTexto);
        obligatoriaCheck.setBackground(new Color(0, 0, 0, 0));

        String[] tiposInsercion = {
                "Al final",
                "Después de actividad específica",
                "Después de última creada"
        };
        tipoInsercionCombo = new JComboBox<>(tiposInsercion);
        styleComboBox(tipoInsercionCombo, colorSecundario);

        actividadPrecedenteField = new JTextField(20);
        styleTextField(actividadPrecedenteField, colorSecundario);
        actividadPrecedenteField.setEnabled(false);

        // Agregar componentes con GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        addLabelAndComponent(datosPanel, "Nombre:", nombreField, gbc, colorTexto);

        gbc.gridx = 0; gbc.gridy = 1;
        addLabelAndComponent(datosPanel, "Descripción:", scrollDescripcion, gbc, colorTexto);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        datosPanel.add(obligatoriaCheck, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        addLabelAndComponent(datosPanel, "Tipo de inserción:", tipoInsercionCombo, gbc, colorTexto);

        gbc.gridx = 0; gbc.gridy = 4;
        addLabelAndComponent(datosPanel, "Actividad precedente:", actividadPrecedenteField, gbc, colorTexto);

        // Panel de botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botonesPanel.setBackground(new Color(colorFondoPrincipal.getRed(),
                colorFondoPrincipal.getGreen(),
                colorFondoPrincipal.getBlue(), 180));

        JButton crearButton = createStyledButton("Crear Actividad", colorAccent);
        JButton intercambiarButton = createStyledButton("Intercambiar Actividades", colorSecundario);

        botonesPanel.add(crearButton);
        botonesPanel.add(intercambiarButton);

        // Agregar paneles al panel principal
        this.add(datosPanel, BorderLayout.CENTER);
        this.add(botonesPanel, BorderLayout.SOUTH);

        // Eventos
        tipoInsercionCombo.addActionListener(e -> {
            actividadPrecedenteField.setEnabled(
                    tipoInsercionCombo.getSelectedIndex() == 1
            );
        });

        crearButton.addActionListener(e -> crearActividad());
        intercambiarButton.addActionListener(e -> mostrarDialogoIntercambio());
    }

    // Métodos auxiliares para estilizar componentes
    private void styleTextField(JTextField textField, Color borderColor) {
        textField.setBorder(BorderFactory.createLineBorder(borderColor));
        textField.setCaretColor(Color.WHITE);
        textField.setForeground(Color.WHITE);
        textField.setBackground(new Color(111, 63, 182));
    }

    private void styleTextArea(JTextArea textArea, Color borderColor) {
        textArea.setBorder(BorderFactory.createLineBorder(borderColor));
        textArea.setCaretColor(Color.WHITE);
        textArea.setForeground(Color.WHITE);
        textArea.setBackground(new Color(111, 63, 182));
    }

    private void styleComboBox(JComboBox<?> comboBox, Color borderColor) {
        comboBox.setBorder(BorderFactory.createLineBorder(borderColor));
        comboBox.setForeground(Color.black);
        comboBox.setBackground(new Color(111, 63, 182));
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        return button;
    }

    private void addLabelAndComponent(JPanel panel, String labelText,
                                      JComponent component, GridBagConstraints gbc, Color textColor) {
        JLabel label = new JLabel(labelText);
        label.setForeground(textColor);
        gbc.gridwidth = 1;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        panel.add(component, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }
    private void crearActividad() {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un proceso primero",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombre = nombreField.getText().trim();
        String descripcion = descripcionArea.getText().trim();
        boolean obligatoria = obligatoriaCheck.isSelected();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            switch (tipoInsercionCombo.getSelectedIndex()) {
                case 0:
                    gestionActividades.insertarActividadAlFinal(procesoId, nombre, descripcion, obligatoria);
                    break;
                case 1:
                    String actividadPrecedente = actividadPrecedenteField.getText().trim();
                    if (!actividadPrecedente.isEmpty()) {
                        gestionActividades.insertarActividadDespuesDe(procesoId, actividadPrecedente, nombre, descripcion, obligatoria);
                    }
                    break;
                case 2:
                    gestionActividades.insertarActividadDespuesDeUltimaCreada(procesoId, nombre, descripcion, obligatoria);
                    break;
            }
            limpiarCampos();
            procesoPanel.notifyActividadCreated(); // Notificar que se creó una actividad
            JOptionPane.showMessageDialog(this,
                    "Actividad creada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear la actividad: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }



    private void mostrarDialogoIntercambio() {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un proceso primero",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> actividades = gestionActividades.obtenerNombresActividades(procesoId);
        if (actividades.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay actividades disponibles para el proceso seleccionado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear y poblar los JComboBox con las actividades
        JComboBox<String> actividad1ComboBox = new JComboBox<>(actividades.toArray(new String[0]));
        JComboBox<String> actividad2ComboBox = new JComboBox<>(actividades.toArray(new String[0]));
        JCheckBox intercambiarTareasCheck = new JCheckBox("Intercambiar tareas junto con las actividades");

        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.add(new JLabel("Primera actividad:"));
        panel.add(actividad1ComboBox);
        panel.add(new JLabel("Segunda actividad:"));
        panel.add(actividad2ComboBox);
        panel.add(intercambiarTareasCheck);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Intercambiar Actividades",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String actividad1 = (String) actividad1ComboBox.getSelectedItem();
            String actividad2 = (String) actividad2ComboBox.getSelectedItem();

            if (actividad1 != null && actividad2 != null && !actividad1.equals(actividad2)) {
                try {
                    gestionActividades.intercambiarActividades(procesoId,
                            actividad1,
                            actividad2,
                            intercambiarTareasCheck.isSelected());

                    JOptionPane.showMessageDialog(this,
                            "Actividades intercambiadas exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    procesoPanel.notifyActividadCreated(); // Actualizar la vista
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al intercambiar actividades: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor seleccione dos actividades diferentes",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        nombreField.setText("");
        descripcionArea.setText("");
        obligatoriaCheck.setSelected(false);
        actividadPrecedenteField.setText("");
        tipoInsercionCombo.setSelectedIndex(0);
    }
}