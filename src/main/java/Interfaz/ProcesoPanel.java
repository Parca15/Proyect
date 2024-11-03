    package Interfaz;

    import Funcionalidades.GestionProcesos;
    import ModelosBase.Proceso;

    import javax.swing.*;
    import javax.swing.border.*;
    import javax.swing.event.ListSelectionListener;
    import java.awt.*;
    import java.awt.event.*;
    import java.util.UUID;

    public class ProcesoPanel extends JPanel {
        private final GestionProcesos gestionProcesos;
        private final JTextField nombreProcesoField;
        private final DefaultListModel<String> procesosListModel;
        private final JList<String> procesosList;
        private UUID selectedProcesoId;
        private java.util.List<Runnable> actividadListeners = new java.util.ArrayList<>();

        // Colores consistentes con el diseño del login
        private Color primaryColor = new Color(147, 112, 219);
        private Color secondaryColor = new Color(138, 43, 226);
        private Color hoverColor = new Color(123, 104, 238);

        private int hoveredIndex = -1;

        public ProcesoPanel(GestionProcesos gestionProcesos) {
            this.gestionProcesos = gestionProcesos;
            setLayout(new BorderLayout());
            // Cambiamos el tamaño preferido a 300x500 para que coincida con el panel izquierdo
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

                    // Crear degradado de tres colores
                    float[] dist = {0.0f, 0.5f, 1.0f};
                    Color[] colors = {primaryColor, new Color(142, 68, 223), secondaryColor};
                    LinearGradientPaint gp = new LinearGradientPaint(0, 0, 0, h, dist, colors);
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, w, h);

                    // Agregar patrón de puntos
                    g2d.setColor(new Color(255, 255, 255, 20));
                    for (int i = 0; i < w; i += 20) {
                        for (int j = 0; j < h; j += 20) {
                            g2d.fillOval(i, j, 2, 2);
                        }
                    }
                }
            };
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setOpaque(false);
            topPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

            // Título con sombra
            JLabel titleLabel = new JLabel("Gestión de Procesos");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            topPanel.add(titleLabel);
            topPanel.add(Box.createVerticalStrut(20));

            // Campo de nombre de proceso
            JLabel nombreLabel = new JLabel("Nombre del Proceso");
            nombreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nombreLabel.setForeground(Color.WHITE);
            nombreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            topPanel.add(nombreLabel);
            topPanel.add(Box.createVerticalStrut(10));

            nombreProcesoField = createStyledTextField();
            nombreProcesoField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            topPanel.add(nombreProcesoField);
            topPanel.add(Box.createVerticalStrut(20));

            // Panel central para la lista
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            centerPanel.setOpaque(false);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));

            // Lista de procesos con estilo personalizado
            procesosListModel = new DefaultListModel<>();
            procesosList = new JList<>(procesosListModel);
            procesosList.setCellRenderer(new CustomListCellRenderer());
            procesosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            procesosList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoveredIndex = -1;
                    procesosList.repaint();
                }
            });

            JScrollPane scrollPane = new JScrollPane(procesosList) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                    super.paintComponent(g);
                }
            };

            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(new RoundedBorder(20));
            centerPanel.add(scrollPane, BorderLayout.CENTER);

            // Panel inferior para botones
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
            bottomPanel.setOpaque(false);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 70, 10));

            // Botones con estilo
            JButton crearButton = createStyledButton("Crear Proceso", 12); // Tamaño de fuente ajustado a 12
            crearButton.setPreferredSize(new Dimension(100, 35));
            JButton eliminarButton = createStyledButton("Eliminar Proceso", 12);
            eliminarButton.setPreferredSize(new Dimension(100, 35));

            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
            bottomPanel.add(crearButton);
            bottomPanel.add(Box.createHorizontalStrut(10)); // Espacio entre botones
            bottomPanel.add(eliminarButton);

            // Agregar todos los paneles al panel principal
            mainPanel.add(topPanel);
            mainPanel.add(centerPanel);
            mainPanel.add(bottomPanel);

            // Agregar el panel principal al ProcesoPanel
            add(mainPanel, BorderLayout.CENTER);

            // Eventos
            crearButton.addActionListener(e -> {
                String nombre = nombreProcesoField.getText().trim();
                if (!nombre.isEmpty()) {
                    Proceso nuevoProceso = gestionProcesos.crearProceso(nombre);
                    procesosListModel.addElement(nombre + " (" + nuevoProceso.getId() + ")");
                    nombreProcesoField.setText("");
                    showSuccessDialog("Proceso creado exitosamente");
                    notifyActividadCreated();
                } else {
                    showErrorDialog("Por favor ingrese un nombre para el proceso");
                }
            });

            eliminarButton.addActionListener(e -> {
                if (selectedProcesoId != null) {
                    int confirmacion = showConfirmDialog("¿Está seguro de eliminar este proceso?");
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        gestionProcesos.eliminarProceso(selectedProcesoId);
                        procesosListModel.remove(procesosList.getSelectedIndex());
                        selectedProcesoId = null;
                        showSuccessDialog("Proceso eliminado exitosamente");
                    }
                } else {
                    showErrorDialog("Por favor seleccione un proceso para eliminar");
                }
            });

            procesosList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && procesosList.getSelectedIndex() != -1) {
                    String selectedItem = procesosList.getSelectedValue();
                    String idStr = selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.indexOf(")"));
                    selectedProcesoId = UUID.fromString(idStr);
                }
            });

            add(mainPanel);
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
            return button;
        }

        private void showSuccessDialog(String message) {
            JDialog dialog = createStyledDialog("Éxito", message, new Color(0, 150, 0));
            dialog.setVisible(true);
        }

        private void showErrorDialog(String message) {
            JDialog dialog = createStyledDialog("Error", message, new Color(150, 0, 0));
            dialog.setVisible(true);
        }

        private int showConfirmDialog(String message) {
            return JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
        }

        private JDialog createStyledDialog(String title, String message, Color baseColor) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(this);
            dialog.setUndecorated(true);

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    GradientPaint gp = new GradientPaint(
                            0, 0, baseColor,
                            0, getHeight(), baseColor.darker()
                    );
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            panel.setLayout(null);

            JLabel messageLabel = new JLabel(message);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            messageLabel.setForeground(Color.WHITE);
            messageLabel.setBounds(20, 20, 260, 60);
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(messageLabel);

            JButton okButton = createStyledButton("Aceptar",20);
            okButton.setBounds(100, 90, 100, 35);
            okButton.addActionListener(e -> dialog.dispose());
            panel.add(okButton);

            dialog.add(panel);
            return dialog;
        }

        // CustomListCellRenderer para estilizar los items de la lista
        private class CustomListCellRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // Configurar el aspecto base del label
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                // Panel personalizado para el fondo con sombra
                JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                        // Color de fondo base
                        Color backgroundColor = new Color(255, 255, 255, 40);

                        // Modificar color si está seleccionado o con hover
                        if (isSelected) {
                            backgroundColor = new Color(255, 255, 255, 80);
                        } else if (index == hoveredIndex) {
                            backgroundColor = new Color(255, 255, 255, 60);
                        }

                        // Dibujar el fondo redondeado
                        g2d.setColor(backgroundColor);
                        g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 10, 10);

                        // Agregar sombra si está con hover o seleccionado
                        if (index == hoveredIndex || isSelected) {
                            // Sombra exterior
                            for (int i = 0; i < 4; i++) {
                                g2d.setColor(new Color(0, 0, 0, 10 - (i * 2)));
                                g2d.drawRoundRect(4 - i, 4 - i, getWidth() - 8 + (i * 2),
                                        getHeight() - 8 + (i * 2), 10, 10);
                            }
                        }

                        g2d.dispose();
                    }
                };

                // Configurar el panel
                panel.setLayout(new BorderLayout());
                panel.setOpaque(false);
                panel.add(label);

                // Configurar colores del texto
                if (isSelected) {
                    label.setForeground(Color.WHITE);
                } else {
                    label.setForeground(Color.black);
                }

                return panel;
            }
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

        // Métodos públicos necesarios
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

        public void addProcesoSelectionListener(ListSelectionListener listener) {
            procesosList.addListSelectionListener(listener);
        }
        public DefaultListModel<String> getListModel() {
            return procesosListModel;
        }
    }