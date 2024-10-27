package Proyecto.Funcionalidades;


import Proyecto.ModelosBase.Proceso;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class ProcessManagementGUI extends JFrame {
    private GestionProcesos gestionProcesos;
    private GestionActividades gestionActividades;
    private GestionTareas gestionTareas;

    private JTextField processNameField;
    private JTextField activityNameField;
    private JTextField activityDescField;
    private JCheckBox activityMandatoryCheck;
    private JComboBox<ProcessItem> processComboBox;
    private JComboBox<String> activityComboBox;
    private JTextField taskDescField;
    private JTextField taskDurationField;
    private JCheckBox taskMandatoryCheck;
    private JTextArea displayArea;

    public ProcessManagementGUI() {
        // Inicializar gestores
        gestionProcesos = new GestionProcesos(new HashMap<>());
        gestionActividades = new GestionActividades(gestionProcesos);
        gestionTareas = new GestionTareas(gestionProcesos);

        // Configurar ventana
        setTitle("Process Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear paneles
        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JPanel processPanel = createProcessPanel();
        JPanel activityPanel = createActivityPanel();
        JPanel taskPanel = createTaskPanel();

        inputPanel.add(processPanel);
        inputPanel.add(activityPanel);
        inputPanel.add(taskPanel);

        // Panel de visualización
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Botón para actualizar vista
        JButton refreshButton = new JButton("Actualizar Vista");
        refreshButton.addActionListener(e -> updateDisplay());

        // Agregar componentes a la ventana
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
    }

    private JPanel createProcessPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Agregar Proceso"));

        processNameField = new JTextField(20);
        JButton addProcessButton = new JButton("Agregar Proceso");

        addProcessButton.addActionListener(e -> {
            String processName = processNameField.getText().trim();
            if (!processName.isEmpty()) {
                Proceso newProcess = gestionProcesos.crearProceso(processName);
                processComboBox.addItem(new ProcessItem(newProcess.getId(), processName));
                processNameField.setText("");
                updateDisplay();
            }
        });

        panel.add(new JLabel("Nombre del Proceso:"));
        panel.add(processNameField);
        panel.add(addProcessButton);

        return panel;
    }

    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Agregar Actividad"));

        processComboBox = new JComboBox<>();
        activityNameField = new JTextField(15);
        activityDescField = new JTextField(20);
        activityMandatoryCheck = new JCheckBox("Obligatoria");
        JButton addActivityButton = new JButton("Agregar Actividad");

        addActivityButton.addActionListener(e -> {
            ProcessItem selectedProcess = (ProcessItem) processComboBox.getSelectedItem();
            if (selectedProcess != null) {
                String name = activityNameField.getText().trim();
                String desc = activityDescField.getText().trim();
                boolean mandatory = activityMandatoryCheck.isSelected();

                if (!name.isEmpty() && !desc.isEmpty()) {
                    gestionActividades.agregarActividad(
                            selectedProcess.getId(),
                            name,
                            desc,
                            mandatory
                    );
                    activityComboBox.addItem(name);
                    activityNameField.setText("");
                    activityDescField.setText("");
                    activityMandatoryCheck.setSelected(false);
                    updateDisplay();
                }
            }
        });

        panel.add(new JLabel("Proceso:"));
        panel.add(processComboBox);
        panel.add(new JLabel("Nombre:"));
        panel.add(activityNameField);
        panel.add(new JLabel("Descripción:"));
        panel.add(activityDescField);
        panel.add(activityMandatoryCheck);
        panel.add(addActivityButton);

        return panel;
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Agregar Tarea"));

        activityComboBox = new JComboBox<>();
        taskDescField = new JTextField(20);
        taskDurationField = new JTextField(5);
        taskMandatoryCheck = new JCheckBox("Obligatoria");
        JButton addTaskButton = new JButton("Agregar Tarea");

        processComboBox.addActionListener(e -> {
            activityComboBox.removeAllItems();
            ProcessItem selectedProcess = (ProcessItem) processComboBox.getSelectedItem();
            if (selectedProcess != null) {
                Proceso proceso = gestionProcesos.buscarProceso(selectedProcess.getId());
                if (proceso != null) {
                    for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                        activityComboBox.addItem(proceso.getActividades().getElementoEnPosicion(i).getNombre());
                    }
                }
            }
        });

        addTaskButton.addActionListener(e -> {
            ProcessItem selectedProcess = (ProcessItem) processComboBox.getSelectedItem();
            String selectedActivity = (String) activityComboBox.getSelectedItem();

            if (selectedProcess != null && selectedActivity != null) {
                String desc = taskDescField.getText().trim();
                String durationText = taskDurationField.getText().trim();

                if (!desc.isEmpty() && !durationText.isEmpty()) {
                    try {
                        int duration = Integer.parseInt(durationText);
                        gestionTareas.agregarTarea(
                                selectedProcess.getId(),
                                selectedActivity,
                                desc,
                                duration,
                                taskMandatoryCheck.isSelected()
                        );
                        taskDescField.setText("");
                        taskDurationField.setText("");
                        taskMandatoryCheck.setSelected(false);
                        updateDisplay();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "La duración debe ser un número entero",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(new JLabel("Actividad:"));
        panel.add(activityComboBox);
        panel.add(new JLabel("Descripción:"));
        panel.add(taskDescField);
        panel.add(new JLabel("Duración (min):"));
        panel.add(taskDurationField);
        panel.add(taskMandatoryCheck);
        panel.add(addTaskButton);

        return panel;
    }

    private void updateDisplay() {
        StringBuilder display = new StringBuilder();
        display.append("PROCESOS Y SUS COMPONENTES:\n\n");

        for (Proceso proceso : gestionProcesos.getProcesos().values()) {
            display.append("Proceso: ").append(proceso.getNombre()).append("\n");

            for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                var actividad = proceso.getActividades().getElementoEnPosicion(i);
                display.append("  ├─ Actividad: ").append(actividad.getNombre())
                        .append(" (").append(actividad.isObligatoria() ? "Obligatoria" : "Opcional").append(")\n")
                        .append("  │  Descripción: ").append(actividad.getDescripcion()).append("\n");

                var tareas = actividad.getTareas();
                if (!tareas.estaVacia()) {
                    var actual = tareas.getNodoPrimero();
                    while (actual != null) {
                        var tarea = actual.getValorNodo();
                        display.append("  │  ├─ Tarea: ").append(tarea.getDescripcion())
                                .append(" (").append(tarea.getDuracion()).append(" min) ")
                                .append(tarea.isObligatoria() ? "Obligatoria" : "Opcional").append("\n");
                        actual = actual.getSiguienteNodo();
                    }
                }
                display.append("  │\n");
            }
            display.append("\n");
        }

        displayArea.setText(display.toString());
    }

    // Clase auxiliar para manejar procesos en el ComboBox
    private static class ProcessItem {
        private final UUID id;
        private final String name;

        public ProcessItem(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProcessManagementGUI gui = new ProcessManagementGUI();
            gui.setVisible(true);
        });
    }
}