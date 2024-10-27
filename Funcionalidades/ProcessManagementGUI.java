package Proyecto.Funcionalidades;

import Proyecto.EstructurasDatos.Nodo;
import Proyecto.ModelosBase.*;
import Proyecto.ModelosBase.Inter.GestorNotificacionesSwing;
import Proyecto.ModelosBase.Notificaciones.MonitorProcesos;
import Proyecto.ModelosBase.Notificaciones.PrioridadNotificacion;
import Proyecto.ModelosBase.Notificaciones.TipoNotificacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.EmptyBorder;

public class ProcessManagementGUI extends JFrame {
    // Mantener las variables existentes
    private GestionProcesos gestionProcesos;
    private GestionActividades gestionActividades;
    private GestionTareas gestionTareas;
    private JPanel mainPanel;
    private JComboBox<String> procesosComboBox;
    private JComboBox<String> actividadesComboBox;
    private Map<String, UUID> procesosMap;
    private DefaultListModel<String> actividadesListModel;
    private DefaultListModel<String> tareasListModel;
    private JList<String> actividadesList;
    private JList<String> tareasList;
    private final GestorNotificacionesSwing gestorNotificaciones;

    // Nuevo esquema de colores
    private static final Color BACKGROUND_COLOR = new Color(242, 232, 207); // F2E8CF - Beige claro
    private static final Color PRIMARY_COLOR = new Color(56, 102, 65);      // 386641 - Verde oscuro
    private static final Color SECONDARY_COLOR = new Color(56, 102, 65, 128);  // Versión más clara del beige
    private static final Color ACCENT_COLOR = new Color(188, 71, 73);       // BC4749 - Rojo
    private static final Color TEXT_COLOR = new Color(33, 37, 41);         // Texto oscuro para mejor legibilidad
    private static final Color BORDER_COLOR = new Color(56, 102, 65, 128); // Verde oscuro con transparencia

    public ProcessManagementGUI() {
        this.gestorNotificaciones = GestorNotificacionesSwing.getInstance();
        gestionProcesos = new GestionProcesos(new HashMap<>());
        gestionActividades = new GestionActividades(gestionProcesos);
        gestionTareas = new GestionTareas(gestionProcesos);
        procesosMap = new HashMap<>();

        GestionNotificaciones.getInstance().setNotificationHandler(notification -> {
            SwingUtilities.invokeLater(() -> {
                gestorNotificaciones.mostrarNotificacion(notification);
            });
        });

        MonitorProcesos.getInstance();

        setupFrame();
        createProcessPanel();
        createActivityPanel();
        createTaskPanel();
        setupListeners();
        applyGlobalStyles();

        pack();
        setLocationRelativeTo(null);
    }

    private void setupFrame() {
        setTitle("Sistema de Gestión de Procesos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        setContentPane(new JScrollPane(mainPanel));
    }
    private void setupListeners() {
        // Listener para el comboBox de procesos
        procesosComboBox.addActionListener(e -> {
            updateActivityList();
            updateActivityComboBox();
            updateTaskList();
        });

        // Listener para el comboBox de actividades
        actividadesComboBox.addActionListener(e -> {
            updateTaskList();
        });

        // Listener para la lista de actividades
        actividadesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedActivity = actividadesList.getSelectedValue();
                if (selectedActivity != null) {
                    actividadesComboBox.setSelectedItem(selectedActivity);
                }
            }
        });
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Agregar efecto hover
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        // Padding del botón
        button.setBorder(BorderFactory.createCompoundBorder(
                button.getBorder(),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));

        return button;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(SECONDARY_COLOR);

        // Crear un borde compuesto con esquinas redondeadas y padding
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        title,
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_COLOR
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        return panel;
    }

    private void createProcessPanel() {
        JPanel processPanel = createSectionPanel("Gestión de Procesos");

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlsPanel.setBackground(SECONDARY_COLOR);

        procesosComboBox = new JComboBox<>();
        JButton createProcessButton = createStyledButton("Crear Proceso");
        JButton deleteProcessButton = createStyledButton("Eliminar Proceso");

        createProcessButton.addActionListener(e -> createProcess());
        deleteProcessButton.addActionListener(e -> deleteProcess());

        controlsPanel.add(procesosComboBox);
        controlsPanel.add(createProcessButton);
        controlsPanel.add(deleteProcessButton);

        processPanel.add(controlsPanel, BorderLayout.CENTER);
        mainPanel.add(processPanel);
    }

    private void createActivityPanel() {
        JPanel activityPanel = createSectionPanel("Gestión de Actividades");

        actividadesListModel = new DefaultListModel<>();
        actividadesList = new JList<>(actividadesListModel);
        JScrollPane scrollPane = new JScrollPane(actividadesList);
        scrollPane.setPreferredSize(new Dimension(0, 200));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(SECONDARY_COLOR);

        JButton createActivityButton = createStyledButton("Crear Actividad");
        JButton addAfterButton = createStyledButton("Agregar Después");
        JButton addToEndButton = createStyledButton("Agregar al Final");
        JButton addAfterLastButton = createStyledButton("Agregar Después de Última");
        JButton swapButton = createStyledButton("Intercambiar");

        createActivityButton.addActionListener(e -> createActivity());
        addAfterButton.addActionListener(e -> insertActivityAfter());
        addToEndButton.addActionListener(e -> insertActivityAtEnd());
        addAfterLastButton.addActionListener(e -> insertActivityAfterLast());
        swapButton.addActionListener(e -> swapActivities());

        buttonPanel.add(createActivityButton);
        buttonPanel.add(addAfterButton);
        buttonPanel.add(addToEndButton);
        buttonPanel.add(addAfterLastButton);
        buttonPanel.add(swapButton);

        activityPanel.add(scrollPane, BorderLayout.CENTER);
        activityPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(activityPanel);
    }

    private void createTaskPanel() {
        JPanel taskPanel = createSectionPanel("Gestión de Tareas");

        tareasListModel = new DefaultListModel<>();
        tareasList = new JList<>(tareasListModel);
        JScrollPane scrollPane = new JScrollPane(tareasList);
        scrollPane.setPreferredSize(new Dimension(0, 200));

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlsPanel.setBackground(SECONDARY_COLOR);

        actividadesComboBox = new JComboBox<>();
        JButton createTaskButton = createStyledButton("Crear Tarea");
        JButton insertTaskButton = createStyledButton("Insertar en Posición");

        createTaskButton.addActionListener(e -> createTask());
        insertTaskButton.addActionListener(e -> insertTaskAtPosition());

        controlsPanel.add(actividadesComboBox);
        controlsPanel.add(createTaskButton);
        controlsPanel.add(insertTaskButton);

        taskPanel.add(scrollPane, BorderLayout.CENTER);
        taskPanel.add(controlsPanel, BorderLayout.SOUTH);

        mainPanel.add(taskPanel);
    }
    private void applyGlobalStyles() {
        // Estilo para ComboBoxes
        procesosComboBox.setBackground(Color.WHITE);
        procesosComboBox.setForeground(TEXT_COLOR);
        procesosComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        procesosComboBox.setPreferredSize(new Dimension(250, 30));

        actividadesComboBox.setBackground(Color.WHITE);
        actividadesComboBox.setForeground(TEXT_COLOR);
        actividadesComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        actividadesComboBox.setPreferredSize(new Dimension(250, 30));

        // Estilo para JLists
        actividadesList.setBackground(SECONDARY_COLOR);
        actividadesList.setForeground(TEXT_COLOR);
        actividadesList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        actividadesList.setSelectionBackground(PRIMARY_COLOR);
        actividadesList.setSelectionForeground(Color.WHITE);

        tareasList.setBackground(SECONDARY_COLOR);
        tareasList.setForeground(TEXT_COLOR);
        tareasList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tareasList.setSelectionBackground(PRIMARY_COLOR);
        tareasList.setSelectionForeground(Color.WHITE);
    }

    private void createProcess() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre del proceso:", "Crear Proceso", JOptionPane.PLAIN_MESSAGE);
        if (nombre != null && !nombre.trim().isEmpty()) {
            try {
                Proceso proceso = gestionProcesos.crearProceso(nombre);
                procesosMap.put(nombre, proceso.getId());
                procesosComboBox.addItem(nombre);
                updateActivityList();

                GestionNotificaciones.getInstance().crearNotificacion(
                        "Proceso Creado",
                        "Se ha creado el proceso: " + nombre,
                        TipoNotificacion.PROCESO_INICIADO,
                        PrioridadNotificacion.MEDIA,
                        proceso.getId().toString()
                );

                // Las notificaciones se generarán automáticamente a través del MonitorProcesos
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al crear el proceso: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteProcess() {
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        if (selectedProcess != null) {
            UUID procesoId = procesosMap.get(selectedProcess);
            gestionProcesos.eliminarProceso(procesoId);
            procesosMap.remove(selectedProcess);
            procesosComboBox.removeItem(selectedProcess);
            actividadesListModel.clear();
            tareasListModel.clear();
        }
    }

    private void createActivity() {
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        if (selectedProcess == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nombreField = new JTextField();
        JTextField descripcionField = new JTextField();
        JCheckBox obligatoriaCheck = new JCheckBox();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Descripción:"));
        panel.add(descripcionField);
        panel.add(new JLabel("Obligatoria:"));
        panel.add(obligatoriaCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Crear Actividad",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            UUID procesoId = procesosMap.get(selectedProcess);
            gestionActividades.agregarActividad(procesoId,
                    nombreField.getText(),
                    descripcionField.getText(),
                    obligatoriaCheck.isSelected());
            updateActivityList();
            updateActivityComboBox();
        }
    }

    private void insertActivityAfter() {
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        if (selectedProcess == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedActivity = actividadesList.getSelectedValue();
        if (selectedActivity == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una actividad después de la cual insertar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nombreField = new JTextField();
        JTextField descripcionField = new JTextField();
        JCheckBox obligatoriaCheck = new JCheckBox();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Descripción:"));
        panel.add(descripcionField);
        panel.add(new JLabel("Obligatoria:"));
        panel.add(obligatoriaCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Insertar Actividad Después",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !nombreField.getText().trim().isEmpty()) {
            UUID procesoId = procesosMap.get(selectedProcess);
            gestionActividades.insertarActividadDespuesDe(procesoId,
                    selectedActivity,
                    nombreField.getText().trim(),
                    descripcionField.getText().trim(),
                    obligatoriaCheck.isSelected());
            updateActivityList();
            updateActivityComboBox();
        }
    }


    private void insertActivityAtEnd() {
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        if (selectedProcess == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nombreField = new JTextField();
        JTextField descripcionField = new JTextField();
        JCheckBox obligatoriaCheck = new JCheckBox();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Descripción:"));
        panel.add(descripcionField);
        panel.add(new JLabel("Obligatoria:"));
        panel.add(obligatoriaCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Insertar Actividad al Final",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !nombreField.getText().trim().isEmpty()) {
            UUID procesoId = procesosMap.get(selectedProcess);
            gestionActividades.insertarActividadAlFinal(procesoId,
                    nombreField.getText().trim(),
                    descripcionField.getText().trim(),
                    obligatoriaCheck.isSelected());
            updateActivityList();
            updateActivityComboBox();
        }
    }

    private void insertActivityAfterLast() {
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        if (selectedProcess == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nombreField = new JTextField();
        JTextField descripcionField = new JTextField();
        JCheckBox obligatoriaCheck = new JCheckBox();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Descripción:"));
        panel.add(descripcionField);
        panel.add(new JLabel("Obligatoria:"));
        panel.add(obligatoriaCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Insertar Actividad Después de la Última",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !nombreField.getText().trim().isEmpty()) {
            UUID procesoId = procesosMap.get(selectedProcess);
            gestionActividades.insertarActividadDespuesDeUltimaCreada(procesoId,
                    nombreField.getText().trim(),
                    descripcionField.getText().trim(),
                    obligatoriaCheck.isSelected());
            updateActivityList();
            updateActivityComboBox();
        }
    }

    private void swapActivities() {
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        if (selectedProcess == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] actividades = new String[actividadesListModel.getSize()];
        for (int i = 0; i < actividadesListModel.getSize(); i++) {
            actividades[i] = actividadesListModel.getElementAt(i);
        }

        JComboBox<String> actividad1ComboBox = new JComboBox<>(actividades);
        JComboBox<String> actividad2ComboBox = new JComboBox<>(actividades);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Primera actividad:"));
        panel.add(actividad1ComboBox);
        panel.add(new JLabel("Segunda actividad:"));
        panel.add(actividad2ComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Intercambiar Actividades",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String actividad1 = (String) actividad1ComboBox.getSelectedItem();
            String actividad2 = (String) actividad2ComboBox.getSelectedItem();

            if (actividad1 != null && actividad2 != null && !actividad1.equals(actividad2)) {
                UUID procesoId = procesosMap.get(selectedProcess);
                gestionActividades.intercambiarActividades(procesoId, actividad1, actividad2);
                updateActivityList();
                updateActivityComboBox();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione dos actividades diferentes.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void updateActivityList() {
        actividadesListModel.clear();
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        if (selectedProcess != null) {
            UUID procesoId = procesosMap.get(selectedProcess);
            Proceso proceso = gestionProcesos.buscarProceso(procesoId);
            if (proceso != null) {
                for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                    Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                    actividadesListModel.addElement(actividad.getNombre());
                }
            }
        }
        updateTaskList(); // Actualizar la lista de tareas cuando cambia la lista de actividades
    }


    private void createTask() {
        String selectedActivity = (String) actividadesComboBox.getSelectedItem();
        if (selectedActivity == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una actividad primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField descripcionField = new JTextField();
        JSpinner duracionSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JCheckBox obligatoriaCheck = new JCheckBox();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Descripción:"));
        panel.add(descripcionField);
        panel.add(new JLabel("Duración (minutos):"));
        panel.add(duracionSpinner);
        panel.add(new JLabel("Obligatoria:"));
        panel.add(obligatoriaCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Crear Tarea",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String selectedProcess = (String) procesosComboBox.getSelectedItem();
            UUID procesoId = procesosMap.get(selectedProcess);
            gestionTareas.agregarTarea(procesoId,
                    selectedActivity,
                    descripcionField.getText(),
                    (Integer) duracionSpinner.getValue(),
                    obligatoriaCheck.isSelected());
            updateTaskList();
        }
    }

    private void insertTaskAtPosition() {
        String selectedActivity = (String) actividadesComboBox.getSelectedItem();
        if (selectedActivity == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una actividad primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get current number of tasks for position validation
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        UUID procesoId = procesosMap.get(selectedProcess);
        Proceso proceso = gestionProcesos.buscarProceso(procesoId);
        int currentTaskCount = 0;

        // Find selected activity and count its tasks
        for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
            Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
            if (actividad.getNombre().equals(selectedActivity)) {
                currentTaskCount = actividad.getTareas().obtenerTamanio();
                break;
            }
        }

        // Create input fields
        JTextField descripcionField = new JTextField();
        JSpinner duracionSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JCheckBox obligatoriaCheck = new JCheckBox();
        JSpinner posicionSpinner = new JSpinner(new SpinnerNumberModel(0, 0, currentTaskCount, 1));

        // Create layout panel
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Descripción:"));
        panel.add(descripcionField);
        panel.add(new JLabel("Duración (minutos):"));
        panel.add(duracionSpinner);
        panel.add(new JLabel("Obligatoria:"));
        panel.add(obligatoriaCheck);
        panel.add(new JLabel("Posición (0-" + currentTaskCount + "):"));
        panel.add(posicionSpinner);

        // Add help text
        JLabel helpText = new JLabel("<html>Posición 0 insertará al inicio.<br>" +
                "Posición " + currentTaskCount + " insertará al final.</html>");
        helpText.setFont(helpText.getFont().deriveFont(Font.ITALIC));
        panel.add(helpText);

        int result = JOptionPane.showConfirmDialog(this, panel, "Insertar Tarea en Posición",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String descripcion = descripcionField.getText().trim();
            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int posicion = (Integer) posicionSpinner.getValue();
            int duracion = (Integer) duracionSpinner.getValue();
            boolean obligatoria = obligatoriaCheck.isSelected();

            try {
                gestionTareas.insertarTareaEnPosicion(
                        procesoId,
                        selectedActivity,
                        posicion,
                        descripcion,
                        duracion,
                        obligatoria
                );
                updateTaskList();
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateActivityComboBox() {
        actividadesComboBox.removeAllItems();
        String selectedProcess = (String) procesosComboBox.getSelectedItem();
        if (selectedProcess != null) {
            UUID procesoId = procesosMap.get(selectedProcess);
            Proceso proceso = gestionProcesos.buscarProceso(procesoId);
            if (proceso != null) {
                for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                    Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                    actividadesComboBox.addItem(actividad.getNombre());
                }
            }
        }
    }

    private void updateTaskList() {
        tareasListModel.clear();
        String selectedActivity = (String) actividadesComboBox.getSelectedItem();
        String selectedProcess = (String) procesosComboBox.getSelectedItem();

        if (selectedActivity != null && selectedProcess != null) {
            UUID procesoId = procesosMap.get(selectedProcess);
            Proceso proceso = gestionProcesos.buscarProceso(procesoId);
            if (proceso != null) {
                for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                    Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                    if (actividad.getNombre().equals(selectedActivity)) {
                        Nodo<Tarea> actual = actividad.getTareas().getNodoPrimero();
                        int index = 0;
                        while (actual != null) {
                            Tarea tarea = actual.getValorNodo();
                            String tareaInfo = String.format("%d. %s (%d min) %s",
                                    index + 1,
                                    tarea.getDescripcion(),
                                    tarea.getDuracion(),
                                    tarea.isObligatoria() ? "[Obligatoria]" : "[Opcional]");
                            tareasListModel.addElement(tareaInfo);
                            actual = actual.getSiguienteNodo();
                            index++;
                        }
                        break;
                    }
                }
            }
        }
    }

    // [Resto de los métodos igual...]

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Configurar propiedades globales de la UI
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.selectionBackground", PRIMARY_COLOR);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("Button.focus", new Color(0, 0, 0, 0));

            // Configurar colores adicionales
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("OptionPane.background", BACKGROUND_COLOR);
            UIManager.put("OptionPane.messageBackground", BACKGROUND_COLOR);
            UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ProcessManagementGUI gui = new ProcessManagementGUI();
            gui.setVisible(true);
        });
    }




}