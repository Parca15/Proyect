package Proyecto.Interfaz;

import Proyecto.Funcionalidades.GestionProcesos;
import Proyecto.ModelosBase.Proceso;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class ProcesoPanel extends JPanel {
    private final GestionProcesos gestionProcesos;
    private final JTextField nombreProcesoField;
    private final DefaultListModel<String> procesosListModel;
    private final JList<String> procesosList;
    private UUID selectedProcesoId;
    private java.util.List<Runnable> actividadListeners = new java.util.ArrayList<>();



    public ProcesoPanel(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Definición de los nuevos colores para mantener consistencia
        Color colorFondoPrincipal = new Color(147, 112, 219);    // Morado principal
        Color colorSecundario = new Color(138, 43, 226);        // Morado más claro
        Color colorAccent = new Color(123, 104, 238);         // Morado claro/lavanda
        Color colorTexto = Color.WHITE;                        // Texto blanco

        // Panel superior para crear procesos
        JPanel creacionPanel = new JPanel(new BorderLayout(5, 5));
        creacionPanel.setBackground(colorFondoPrincipal);

        // Crear un TitledBorder directamente
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorAccent, 2),
                "Crear Nuevo Proceso"
        );
        titledBorder.setTitleColor(colorTexto);
        titledBorder.setTitleFont(new Font("Dialog", Font.BOLD, 14));
        creacionPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        nombreProcesoField = new JTextField(20);
        nombreProcesoField.setBackground(colorSecundario.darker());
        nombreProcesoField.setForeground(colorTexto);
        nombreProcesoField.setCaretColor(colorTexto);
        nombreProcesoField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorAccent),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        nombreProcesoField.setFont(new Font("Dialog", Font.PLAIN, 14));

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(colorTexto);
        lblNombre.setFont(new Font("Dialog", Font.BOLD, 14));

        JButton crearButton = new JButton("Crear Proceso");
        crearButton.setBackground(colorAccent);
        crearButton.setForeground(colorTexto);
        crearButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorAccent.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        crearButton.setFocusPainted(false);
        crearButton.setFont(new Font("Dialog", Font.BOLD, 14));

        creacionPanel.add(lblNombre, BorderLayout.WEST);
        creacionPanel.add(nombreProcesoField, BorderLayout.CENTER);
        creacionPanel.add(crearButton, BorderLayout.EAST);

        // Panel central para lista de procesos
        procesosListModel = new DefaultListModel<>();
        procesosList = new JList<>(procesosListModel);
        procesosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        procesosList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        procesosList.setBackground(colorSecundario);
        procesosList.setForeground(colorTexto);
        procesosList.setSelectionBackground(colorAccent);
        procesosList.setSelectionForeground(colorTexto);
        procesosList.setFont(new Font("Dialog", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(procesosList);

        // Crear otro TitledBorder para el scrollPane
        TitledBorder scrollPaneBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorAccent, 2),
                "Procesos Existentes"
        );
        scrollPaneBorder.setTitleColor(Color.black);
        scrollPaneBorder.setTitleFont(new Font("Dialog", Font.BOLD, 14));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                scrollPaneBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(colorFondoPrincipal);

        // Panel inferior para acciones
        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accionesPanel.setBackground(colorFondoPrincipal);
        accionesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton eliminarButton = new JButton("Eliminar Proceso");
        eliminarButton.setBackground(colorAccent);
        eliminarButton.setForeground(colorTexto);
        eliminarButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorAccent.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        eliminarButton.setFocusPainted(false);
        eliminarButton.setFont(new Font("Dialog", Font.BOLD, 14));
        accionesPanel.add(eliminarButton);

        // Establecer el color de fondo del panel principal
        this.setBackground(colorFondoPrincipal);

        // Agregar los paneles al panel principal
        this.add(creacionPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(accionesPanel, BorderLayout.SOUTH);

        // Personalizar los JOptionPane para mensajes
        UIManager.put("OptionPane.background", colorFondoPrincipal);
        UIManager.put("OptionPane.messageForeground", colorTexto);
        UIManager.put("Panel.background", colorFondoPrincipal);
        UIManager.put("OptionPane.messageFont", new Font("Dialog", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Dialog", Font.BOLD, 14));

        // Eventos
        crearButton.addActionListener(e -> {
            String nombre = nombreProcesoField.getText().trim();
            if (!nombre.isEmpty()) {
                Proceso nuevoProceso = gestionProcesos.crearProceso(nombre);
                procesosListModel.addElement(nombre + " (" + nuevoProceso.getId() + ")");
                nombreProcesoField.setText("");
                JOptionPane.showMessageDialog(this,
                        "Proceso creado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                // Notificar a los listeners de actividad que se ha creado un nuevo proceso
                notifyActividadCreated();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor ingrese un nombre para el proceso",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        procesosList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && procesosList.getSelectedIndex() != -1) {
                String selectedItem = procesosList.getSelectedValue();
                String idStr = selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.indexOf(")"));
                selectedProcesoId = UUID.fromString(idStr);
            }
        });

        eliminarButton.addActionListener(e -> {
            if (selectedProcesoId != null) {
                gestionProcesos.eliminarProceso(selectedProcesoId);
                procesosListModel.remove(procesosList.getSelectedIndex());
                selectedProcesoId = null;
                JOptionPane.showMessageDialog(this,
                        "Proceso eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor seleccione un proceso para eliminar",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Efectos hover para los botones
        configureButtonHover(crearButton, colorAccent, colorSecundario);
        configureButtonHover(eliminarButton, colorAccent, colorSecundario);
    }

    private void configureButtonHover(JButton button, Color defaultColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
            }
        });
    }
    public UUID getSelectedProcesoId() {
        return selectedProcesoId;
    }
    public void addActividadListener(Runnable listener) {
        actividadListeners.add(listener);
    }

    public void notifyActividadCreated() {
        for (Runnable listener : actividadListeners) {
            listener.run();
        }
    }
    public GestionProcesos getGestionProcesos() {
        return gestionProcesos;
    }

    /**
     * Adds a ListSelectionListener to the procesos list.
     * This method allows other components to listen for selection changes in the process list.
     *
     * @param listener The ListSelectionListener to be added
     */
    public void addProcesoSelectionListener(ListSelectionListener listener) {
        procesosList.addListSelectionListener(listener);
    }
}