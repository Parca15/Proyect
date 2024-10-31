package Interfaz;

import Funcionalidades.GestionActividades;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.UUID;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class ActividadPanel extends JPanel {
    private final GestionActividades gestionActividades;
    private final ProcesoPanel procesoPanel;
    private final JTextField nombreField;
    private final JTextArea descripcionArea;
    private final JCheckBox obligatoriaCheck;
    private final JComboBox<String> tipoInsercionCombo;
    private final JTextField actividadPrecedenteField;

    // Colores consistentes con ProcesoPanel
    private Color primaryColor = new Color(147, 112, 219);
    private Color secondaryColor = new Color(138, 43, 226);
    private Color hoverColor = new Color(123, 104, 238);

    public ActividadPanel(GestionActividades gestionActividades, ProcesoPanel procesoPanel) {
        this.gestionActividades = gestionActividades;
        this.procesoPanel = procesoPanel;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 500));

        // Panel principal con degradado
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                float[] dist = {0.0f, 0.5f, 1.0f};
                Color[] colors = {primaryColor, new Color(142, 68, 223), secondaryColor};
                LinearGradientPaint gp = new LinearGradientPaint(0, 0, 0, h, dist, colors);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < w; i += 20) {
                    for (int j = 0; j < h; j += 20) {
                        g2d.fillOval(i, j, 2, 2);
                    }
                }
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Panel superior con título y campos
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Título
        JLabel titleLabel = new JLabel("Gestión de Actividades");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(20));

        // Campo nombre
        JLabel nombreLabel = new JLabel("Nombre de la Actividad");
        nombreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nombreLabel.setForeground(Color.WHITE);
        nombreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(nombreLabel);
        topPanel.add(Box.createVerticalStrut(10));

        nombreField = createStyledTextField();
        nombreField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        topPanel.add(nombreField);
        topPanel.add(Box.createVerticalStrut(15));

        // Campo descripción
        JLabel descripcionLabel = new JLabel("Descripción");
        descripcionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descripcionLabel.setForeground(Color.WHITE);
        descripcionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(descripcionLabel);
        topPanel.add(Box.createVerticalStrut(10));

        descripcionArea = createStyledTextArea();
        JScrollPane scrollPane = new JScrollPane(descripcionArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new RoundedBorder(20));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        topPanel.add(scrollPane);
        topPanel.add(Box.createVerticalStrut(15));

        // Checkbox obligatoria
        obligatoriaCheck = new JCheckBox("Obligatoria") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 40));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        obligatoriaCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        obligatoriaCheck.setForeground(Color.WHITE);
        obligatoriaCheck.setOpaque(false);
        obligatoriaCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(obligatoriaCheck);
        topPanel.add(Box.createVerticalStrut(15));

        // Combo tipo inserción
        JLabel tipoLabel = new JLabel("Tipo de Inserción");
        tipoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tipoLabel.setForeground(Color.WHITE);
        tipoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(tipoLabel);
        topPanel.add(Box.createVerticalStrut(10));

        String[] tiposInsercion = {"Al final", "Después de actividad específica", "Después de última creada"};
        tipoInsercionCombo = createStyledComboBox(tiposInsercion);
        tipoInsercionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        topPanel.add(tipoInsercionCombo);
        topPanel.add(Box.createVerticalStrut(15));

        // Campo actividad precedente
        JLabel precedenteLabel = new JLabel("Actividad Precedente");
        precedenteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        precedenteLabel.setForeground(Color.WHITE);
        precedenteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(precedenteLabel);
        topPanel.add(Box.createVerticalStrut(10));

        actividadPrecedenteField = createStyledTextField();
        actividadPrecedenteField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        actividadPrecedenteField.setEnabled(false);
        topPanel.add(actividadPrecedenteField);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JButton crearButton = createStyledButton("Crear", 12);
        JButton intercambiarButton = createStyledButton("Intercambiar", 12);

        buttonPanel.add(crearButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(intercambiarButton);

        // Agregar paneles al panel principal
        mainPanel.add(topPanel);
        mainPanel.add(buttonPanel);
        add(mainPanel);

        // Eventos
        tipoInsercionCombo.addActionListener(e -> {
            actividadPrecedenteField.setEnabled(tipoInsercionCombo.getSelectedIndex() == 1);
        });

        crearButton.addActionListener(e -> crearActividad());
        intercambiarButton.addActionListener(e -> mostrarDialogoIntercambio());
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2d.dispose();

                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2d.dispose();
            }
        };

        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        area.setOpaque(false);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        return area;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setForeground(Color.black);
        comboBox.setBackground(new Color(255, 255, 255, 40));
        comboBox.setBorder(new RoundedBorder(20));
        ((JComponent) comboBox.getRenderer()).setOpaque(false);

        return comboBox;
    }

    private JButton createStyledButton(String text, int fontSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(hoverColor);
                } else {
                    g2d.setColor(primaryColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    private static class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }
    }

    // Mantener los métodos existentes sin cambios
    private void crearActividad() {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un proceso primero",
                    "Error",
                    ERROR_MESSAGE);
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
                    ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoIntercambio() {
        UUID procesoId = procesoPanel.getSelectedProcesoId();
        if (procesoId == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un proceso primero",
                    "Error",
                    ERROR_MESSAGE);
            return;
        }

        List<String> actividades = gestionActividades.obtenerNombresActividades(procesoId);
        if (actividades.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay actividades disponibles para el proceso seleccionado",
                    "Error",
                    ERROR_MESSAGE);
            return;
        }

        // Crear y poblar los JComboBox con las actividades
        JComboBox<String> actividad1ComboBox = new JComboBox<>(actividades.toArray(new String[0]));
        JComboBox<String> actividad2ComboBox = new JComboBox<>(actividades.toArray(new String[0]));
        JCheckBox intercambiarTareasCheck = new JCheckBox("Intercambiar tareas junto con las actividades");
        intercambiarTareasCheck.setForeground(Color.WHITE);
        intercambiarTareasCheck.setBackground(new Color(255, 255, 255, 40));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                GradientPaint gp = new GradientPaint(0, 0, primaryColor, 0, h, secondaryColor);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 20, 20);
            }
        };
        panel.setLayout(new GridLayout(5, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        // Crear labels estilizados
        JLabel label1 = new JLabel("Primera actividad:");
        JLabel label2 = new JLabel("Segunda actividad:");
        label1.setForeground(Color.WHITE);
        label2.setForeground(Color.WHITE);
        label1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label2.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Estilizar los ComboBox
        actividad1ComboBox.setBackground(new Color(255, 255, 255, 40));
        actividad2ComboBox.setBackground(new Color(255, 255, 255, 40));
        actividad1ComboBox.setForeground(Color.WHITE);
        actividad2ComboBox.setForeground(Color.WHITE);

        panel.add(label1);
        panel.add(actividad1ComboBox);
        panel.add(label2);
        panel.add(actividad2ComboBox);
        panel.add(intercambiarTareasCheck);

        // Crear un JOptionPane personalizado
        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);

        // Crear y mostrar el diálogo
        JDialog dialog = optionPane.createDialog(this, "Intercambiar Actividades");
        dialog.setBackground(new Color(147, 112, 219));

        // Mostrar el diálogo y procesar el resultado
        dialog.setVisible(true);
        Object selectedValue = optionPane.getValue();

        if (selectedValue != null && (Integer) selectedValue == JOptionPane.OK_OPTION) {
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
                            ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor seleccione dos actividades diferentes",
                        "Error",
                        ERROR_MESSAGE);
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