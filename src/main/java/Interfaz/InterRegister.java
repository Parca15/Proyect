package Interfaz;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.*;
import java.util.regex.*;

public class InterRegister extends JFrame {
    private JTextField nombreField;
    private JTextField apellidoField;
    private JTextField documentoField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField telefonoField;
    private Color primaryColor = new Color(147, 112, 219);
    private Color secondaryColor = new Color(138, 43, 226);
    private Color hoverColor = new Color(123, 104, 238);
    private Point initialClick;

    public InterRegister() {
        setTitle("Registro de Usuario");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

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
        mainPanel.setLayout(null);

        // Hacer la ventana draggable
        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                setLocation(thisX + e.getX() - initialClick.x, thisY + e.getY() - initialClick.y);
            }
        });

        // Título con sombra
        JLabel titleLabel = new JLabel("Registro de Usuario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(100, 30, 250, 40);
        mainPanel.add(titleLabel);

        // Campos de registro
        int yOffset = 90;
        int spacing = 80;

        // Nombre
        addFormField(mainPanel, "Nombre", nombreField = createStyledTextField(), yOffset);

        // Apellido
        addFormField(mainPanel, "Apellido", apellidoField = createStyledTextField(), yOffset + spacing);

        // Documento
        addFormField(mainPanel, "Documento", documentoField = createStyledTextField(), yOffset + spacing * 2);
        setNumericFilter(documentoField);

        // Contraseña
        addFormField(mainPanel, "Contraseña", passwordField = createStyledPasswordField(), yOffset + spacing * 3);

        // Email
        addFormField(mainPanel, "Email", emailField = createStyledTextField(), yOffset + spacing * 4);

        // Teléfono
        addFormField(mainPanel, "Teléfono", telefonoField = createStyledTextField(), yOffset + spacing * 5);
        setNumericFilter(telefonoField);

        // Botón de registro
        JButton registerButton = createStyledButton("Registrarse");
        registerButton.setBounds(50, yOffset + spacing * 6, 300, 40);
        registerButton.addActionListener(e -> handleRegister());
        mainPanel.add(registerButton);

        // Link para volver al login
        JLabel backToLogin = createHoverLabel("Volver al inicio de sesión");
        backToLogin.setBounds(130, yOffset + spacing * 6 + 50, 150, 30);
        backToLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new InterLogin().setVisible(true);
            }
        });
        mainPanel.add(backToLogin);

        // Botón de cierre
        JButton closeButton = createCloseButton();
        closeButton.setBounds(350, 10, 30, 30);
        mainPanel.add(closeButton);

        // Agregar sombra al panel principal
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1, true)
        ));

        add(mainPanel);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, int yPos) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBounds(50, yPos, 200, 30);
        panel.add(label);

        field.setBounds(50, yPos + 30, 300, 40);
        panel.add(field);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        return field;
    }

    private void setNumericFilter(JTextField textField) {
        ((PlainDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void handleRegister() {
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String documento = documentoField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        String telefono = telefonoField.getText();

        // Validaciones
        if (nombre.isEmpty() || apellido.isEmpty() || documento.isEmpty() ||
                password.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos",
                    "Error de registro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un email válido",
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            File file = new File("src/main/resources/Login_Archivo/Usuarios");
            file.getParentFile().mkdirs(); // Crear directorio si no existe

            // Verificar si el documento ya existe
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("@@");
                    if (parts.length > 2 && parts[2].equals(documento)) {
                        reader.close();
                        JOptionPane.showMessageDialog(this,
                                "El documento ya está registrado",
                                "Error de registro",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                reader.close();
            }

            // Guardar nuevo usuario
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            String userData = String.format("%s@@%s@@%s@@%s@@%s@@%s",
                    nombre, apellido, documento, password, email, telefono);

            if (file.length() > 0) {
                bw.newLine();
            }
            bw.write(userData);
            bw.close();

            JOptionPane.showMessageDialog(this,
                    "Registro exitoso",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // Volver al login
            dispose();
            new InterLogin().setVisible(true);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el usuario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isPressed) {
                    g2d.setColor(hoverColor.darker());
                } else if (isHovered) {
                    g2d.setColor(hoverColor);
                } else {
                    g2d.setColor(primaryColor);
                }

                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), (getWidth() - textWidth) / 2,
                        (getHeight() + textHeight / 2) / 2);
            }
        };
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createHoverLabel(String text) {
        JLabel label = new JLabel(text) {
            private boolean isHovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        setForeground(hoverColor);
                    }
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        setForeground(Color.WHITE);
                    }
                });
            }
        };
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    private JButton createCloseButton() {
        JButton button = new JButton("×") {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isHovered) {
                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString("×", (getWidth() - fm.stringWidth("×")) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
            }
        };
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> System.exit(0));
        return button;
    }

    public class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
    }
    private void openRegisterWindow() {
        dispose(); // Cierra la ventana actual
        new InterRegister().setVisible(true); // Abre la ventana de registro
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new InterRegister().setVisible(true);
        });
    }
}