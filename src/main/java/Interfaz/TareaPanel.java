    package Interfaz;

    import javax.swing.*;
    import javax.swing.plaf.basic.BasicTabbedPaneUI;
    import java.awt.*;

    import Controller.TareaController;
    import Funcionalidades.GestionTareas;
    import Funcionalidades.GestionConsultas;

    public class TareaPanel extends JPanel {
        private final TareaController controller;
        private final JTextField descripcionField;
        private final JSpinner duracionSpinner;
        private final JCheckBox obligatoriaCheck;
        private final JComboBox<String> actividadCombo;
        private final JComboBox<String> tipoInsercionCombo;
        private final JSpinner posicionSpinner;
        private final JComboBox<String> tipoBusquedaCombo;
        private final JComboBox<String> actividadBusquedaCombo;
        private final JTextArea resultadosBusqueda;
        private final JButton crearButton;
        private final JButton buscarButton;
        private final JButton calcularButton;
        private final JLabel resultadoDuracion;

        // Colores consistentes con ActividadPanel
        private Color primaryColor = new Color(147, 112, 219);
        private Color secondaryColor = new Color(138, 43, 226);
        private Color hoverColor = new Color(123, 104, 238);

        public TareaPanel(GestionTareas gestionTareas, GestionConsultas gestionConsultas, ProcesoPanel procesoPanel) {
            controller = new TareaController(gestionTareas, gestionConsultas, procesoPanel);
            controller.setView(this);

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(300, 500));

            // Inicialización de componentes
            descripcionField = createStyledTextField();
            duracionSpinner = createStyledSpinner(new SpinnerNumberModel(30, 1, 300, 1));
            obligatoriaCheck = createStyledCheckBox();
            actividadCombo = createStyledComboBox(new String[]{});
            tipoInsercionCombo = createStyledComboBox(new String[]{"Al final", "En posición"});
            posicionSpinner = createStyledSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            tipoBusquedaCombo = createStyledComboBox(new String[]{"Por proceso", "Actividad reciente", "Por actividad"});
            actividadBusquedaCombo = createStyledComboBox(new String[]{});
            resultadosBusqueda = createStyledTextArea();
            crearButton = createStyledButton("Crear Tarea");
            buscarButton = createStyledButton("Buscar");
            calcularButton = createStyledButton("Calcular");
            resultadoDuracion = createStyledLabel();

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

                    // Efecto de puntos decorativos
                    g2d.setColor(new Color(255, 255, 255, 20));
                    for (int i = 0; i < w; i += 20) {
                        for (int j = 0; j < h; j += 20) {
                            g2d.fillOval(i, j, 2, 2);
                        }
                    }
                }
            };
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setOpaque(false);

            // Crear pestañas con el nuevo estilo y hacerlas transparentes
            JTabbedPane tabbedPane = createStyledTabbedPane();
            tabbedPane.setOpaque(false);
            UIManager.put("TabbedPane.contentOpaque", false);

            // Configurar UI personalizado para el TabbedPane
            tabbedPane.setUI(new BasicTabbedPaneUI() {
                @Override
                protected void installDefaults() {
                    super.installDefaults();
                    contentBorderInsets = new Insets(0, 0, 0, 0);
                    tabAreaInsets = new Insets(0, 0, 0, 0);
                }

                @Override
                protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                    // No pintar el borde del contenido
                }
            });

            // Panel de creación con transparencia
            JPanel creacionPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    // No llamar a super.paintComponent para mantener transparencia
                }
            };
            creacionPanel.setLayout(new BoxLayout(creacionPanel, BoxLayout.Y_AXIS));
            creacionPanel.setOpaque(false);
            creacionPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

            // Agregar componentes al panel de creación
            addComponentToPanel(creacionPanel, "Descripción", descripcionField);
            addComponentToPanel(creacionPanel, "Duración (min)", duracionSpinner);
            addComponentToPanel(creacionPanel, "Actividad", actividadCombo);
            addComponentToPanel(creacionPanel, "Tipo de inserción", tipoInsercionCombo);
            addComponentToPanel(creacionPanel, "Posición", posicionSpinner);
            creacionPanel.add(Box.createVerticalStrut(10));
            creacionPanel.add(obligatoriaCheck);
            creacionPanel.add(Box.createVerticalStrut(20));
            creacionPanel.add(crearButton);

            // Hacer transparentes los paneles contenedores en creacionPanel
            for (Component comp : creacionPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    ((JPanel) comp).setOpaque(false);
                }
            }

            // Panel de búsqueda con transparencia
            JPanel busquedaPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    // No llamar a super.paintComponent para mantener transparencia
                }
            };
            busquedaPanel.setLayout(new BoxLayout(busquedaPanel, BoxLayout.Y_AXIS));
            busquedaPanel.setOpaque(false);
            busquedaPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

            addComponentToPanel(busquedaPanel, "Tipo de búsqueda", tipoBusquedaCombo);
            addComponentToPanel(busquedaPanel, "Actividad", actividadBusquedaCombo);
            busquedaPanel.add(Box.createVerticalStrut(10));
            busquedaPanel.add(buscarButton);
            busquedaPanel.add(Box.createVerticalStrut(10));

            // Hacer transparentes los paneles contenedores en busquedaPanel
            for (Component comp : busquedaPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    ((JPanel) comp).setOpaque(false);
                }
            }

            JScrollPane scrollPane = new JScrollPane(resultadosBusqueda);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(new InterRegister.RoundedBorder(20));
            busquedaPanel.add(scrollPane);

            // Panel de cálculo con transparencia
            JPanel calculoPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    // No llamar a super.paintComponent para mantener transparencia
                }
            };
            calculoPanel.setLayout(new BoxLayout(calculoPanel, BoxLayout.Y_AXIS));
            calculoPanel.setOpaque(false);
            calculoPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

            calculoPanel.add(calcularButton);
            calculoPanel.add(Box.createVerticalStrut(20));
            calculoPanel.add(resultadoDuracion);

            // Hacer transparentes los paneles contenedores en calculoPanel
            for (Component comp : calculoPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    ((JPanel) comp).setOpaque(false);
                }
            }

            // Agregar paneles a las pestañas
            tabbedPane.addTab("Crear", creacionPanel);
            tabbedPane.addTab("Buscar", busquedaPanel);
            tabbedPane.addTab("Calcular", calculoPanel);

            mainPanel.add(tabbedPane);
            add(mainPanel);

            // === Eventos ===
            setupEventListeners(procesoPanel);
        }

        private JTabbedPane createStyledTabbedPane() {
            JTabbedPane tabbedPane = new JTabbedPane() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Fondo semi-transparente
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                    super.paintComponent(g);
                }
            };

            tabbedPane.setOpaque(false);
            tabbedPane.setForeground(Color.black);
            tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

            return tabbedPane;
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

        private JSpinner createStyledSpinner(SpinnerNumberModel model) {
            JSpinner spinner = new JSpinner(model) {
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

            spinner.setOpaque(false);
            spinner.setBorder(new InterRegister.RoundedBorder(20));
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
                tf.setForeground(Color.WHITE);
                tf.setCaretColor(Color.WHITE);
                tf.setOpaque(false);
                tf.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            }

            return spinner;
        }

        private JComboBox<String> createStyledComboBox(String[] items) {
            JComboBox<String> comboBox = new JComboBox<>(items) {
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

            comboBox.setOpaque(false);
            comboBox.setForeground(Color.WHITE);
            comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            comboBox.setBorder(new InterRegister.RoundedBorder(20));

            return comboBox;
        }

        private JCheckBox createStyledCheckBox() {
            JCheckBox checkBox = new JCheckBox("Obligatoria") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (isSelected()) {
                        g2d.setColor(new Color(255, 255, 255, 80));
                    } else {
                        g2d.setColor(new Color(255, 255, 255, 40));
                    }
                    g2d.fillRoundRect(0, 0, 20, 20, 5, 5);
                    g2d.dispose();

                    super.paintComponent(g);
                }
            };

            checkBox.setOpaque(false);
            checkBox.setForeground(Color.WHITE);
            checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            return checkBox;
        }

        private JButton createStyledButton(String text) {
            JButton button = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isPressed()) {
                        g2d.setColor(new Color(255, 255, 255, 60));
                    } else if (getModel().isRollover()) {
                        g2d.setColor(hoverColor);
                    } else {
                        g2d.setColor(primaryColor);
                    }
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                    FontMetrics metrics = g2d.getFontMetrics(getFont());
                    int textWidth = metrics.stringWidth(getText());
                    int textHeight = metrics.getHeight();
                    int x = (getWidth() - textWidth) / 2;
                    int y = ((getHeight() - textHeight) / 2) + metrics.getAscent();

                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2d.drawString(getText(), x, y);
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

            button.setOpaque(false);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setPreferredSize(new Dimension(200, 40));

            return button;
        }

        private JLabel createStyledLabel() {
            JLabel label = new JLabel("");
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }

        private void addComponentToPanel(JPanel panel, String labelText, JComponent component) {
            JPanel wrapper = new JPanel();
            wrapper.setOpaque(false);
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

            JLabel label = new JLabel(labelText);
            label.setForeground(Color.black);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));

            wrapper.add(label);
            wrapper.add(Box.createVerticalStrut(5));
            wrapper.add(component);
            wrapper.add(Box.createVerticalStrut(10));

            panel.add(wrapper);
        }

        private void setupEventListeners(ProcesoPanel procesoPanel) {
            procesoPanel.addActividadListener(() -> {
                SwingUtilities.invokeLater(controller::actualizarComboBoxActividades);
            });

            procesoPanel.addProcesoSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    SwingUtilities.invokeLater(controller::actualizarComboBoxActividades);
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
                try {
                    controller.crearTarea(
                            (String) actividadCombo.getSelectedItem(),
                            descripcionField.getText().trim(),
                            (Integer) duracionSpinner.getValue(),
                            obligatoriaCheck.isSelected(),
                            tipoInsercionCombo.getSelectedIndex(),
                            (Integer) posicionSpinner.getValue()
                    );
                    limpiarCampos();
                    mostrarExito();
                    SwingUtilities.invokeLater(controller::actualizarComboBoxActividades);
                } catch (Exception ex) {
                    mostrarError("Error al crear la tarea: " + ex.getMessage());
                }
            });

            buscarButton.addActionListener(e -> {
                try {
                    String resultado = controller.buscarTareas(
                            tipoBusquedaCombo.getSelectedIndex(),
                            (String) actividadBusquedaCombo.getSelectedItem()
                    );
                    resultadosBusqueda.setText(resultado);
                } catch (Exception ex) {
                    mostrarError("Error al buscar tareas: " + ex.getMessage());
                }
            });

            calcularButton.addActionListener(e -> {
                try {
                    GestionConsultas.TiempoProceso tiempo = controller.calcularDuracionProceso();
                    resultadoDuracion.setText(String.format(
                            "Duración mínima: %d minutos\nDuración máxima: %d minutos",
                            tiempo.getTiempoMinimo(),
                            tiempo.getTiempoMaximo()
                    ));
                } catch (Exception ex) {
                    mostrarError(ex.getMessage());
                }
            });
        }

        // Métodos públicos para el controller
        public void limpiarComboBoxes() {
            actividadCombo.removeAllItems();
            actividadBusquedaCombo.removeAllItems();
        }

        public void agregarActividad(String nombreActividad) {
            actividadCombo.addItem(nombreActividad);
            actividadBusquedaCombo.addItem(nombreActividad);
        }

        public void seleccionarPrimeraActividad() {
            if (actividadCombo.getItemCount() > 0) {
                actividadCombo.setSelectedIndex(0);
                actividadBusquedaCombo.setSelectedIndex(0);
            } else {
                actividadCombo.setSelectedIndex(-1);
                actividadBusquedaCombo.setSelectedIndex(-1);
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

        private void mostrarExito() {
            JOptionPane.showMessageDialog(this, "Tarea creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }