package Interfaz;

import Interfaz.MainApplication;
import App.Login;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.border.*;

public class InterLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPassword;
    private Color primaryColor = new Color(147, 112, 219);
    private Color secondaryColor = new Color(138, 43, 226);
    private Color hoverColor = new Color(123, 104, 238);
    private Point initialClick;
    private boolean passwordVisible = false;

    public InterLogin() {
        setTitle("Inicio de sesión");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Panel principal con degradado mejorado
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

                int xMoved = thisX + e.getX() - initialClick.x;
                int yMoved = thisY + e.getY() - initialClick.y;

                setLocation(xMoved, yMoved);
            }
        });

        // Avatar/Icono de usuario
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujar círculo blanco de fondo
                g2d.setColor(Color.WHITE);
                g2d.fillOval(0, 0, 60, 60);

                // Dibujar silueta del usuario
                g2d.setColor(primaryColor);
                g2d.fillOval(20, 10, 20, 20); // Cabeza
                g2d.fillOval(10, 25, 40, 40); // Cuerpo
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setBounds(170, 20, 60, 60);
        mainPanel.add(avatarPanel);

        // Título con sombra
        JLabel titleLabel = new JLabel("Inicio de sesión");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(120, 90, 200, 40);
        mainPanel.add(titleLabel);

        // Campo de documento
        JLabel docLabel = new JLabel("Documento");
        docLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        docLabel.setForeground(Color.WHITE);
        docLabel.setBounds(50, 140, 200, 30);
        mainPanel.add(docLabel);

        emailField = createStyledTextField();
        emailField.setBounds(50, 170, 300, 40);
        setNumericFilter(emailField);
        mainPanel.add(emailField);

        // Campo de contraseña
        JLabel passLabel = new JLabel("Contraseña");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50, 220, 200, 30);
        mainPanel.add(passLabel);

        JPanel passwordPanel = createPasswordPanel();
        passwordPanel.setBounds(50, 250, 300, 40);
        mainPanel.add(passwordPanel);

        // Checkbox personalizado
        showPassword = new JCheckBox("Mostrar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibuja el cuadro del checkbox
                if (isSelected()) {
                    // Dibuja el ojo abierto
                    g2d.setColor(Color.WHITE);
                    g2d.drawOval(4, 11, 12, 8);
                    g2d.fillOval(7, 11, 6, 8);
                } else {
                    // Dibuja el ojo con una línea diagonal
                    g2d.setColor(Color.WHITE);
                    g2d.drawOval(4, 11, 12, 8);
                    g2d.drawLine(4, 11, 16, 19);
                }
                // Dibuja el texto "Mostrar"
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), 20, ((getHeight() - fm.getHeight()) / 2) + fm.getAscent());

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(getFont());
                return new Dimension(fm.stringWidth(getText()) + 24, Math.max(fm.getHeight() + 4, 20));
            }
        };

        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassword.setBounds(50, 300, 120, 30);
        showPassword.setForeground(Color.WHITE);
        showPassword.setOpaque(false);
        showPassword.setFocusPainted(false);
        showPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));

        showPassword.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            passwordField.setEchoChar(passwordVisible ? (char) 0 : '●');
            showPassword.repaint();
        });
        mainPanel.add(showPassword);

        // Link "Olvidó su contraseña"
        JLabel forgetPassword = createHoverLabel("¿Olvidó su contraseña?");
        forgetPassword.setBounds(220, 300, 130, 30);
        mainPanel.add(forgetPassword);

        // Botón de inicio de sesión
        JButton loginButton = createStyledButton("Iniciar sesión");
        loginButton.setBounds(50, 350, 300, 40);
        loginButton.addActionListener(e -> handleLogin());
        mainPanel.add(loginButton);

        // Links de registro
        JLabel noAccountLabel = new JLabel("¿No tienes cuenta?");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noAccountLabel.setForeground(Color.WHITE);
        noAccountLabel.setBounds(90, 400, 120, 30);
        mainPanel.add(noAccountLabel);

        JLabel registerLink = createHoverLabel("Registrar");
        registerLink.setBounds(210, 400, 100, 30);
        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                verificarAdministrador();
            }
        });
        mainPanel.add(registerLink);

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

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujamos el fondo
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                // Dibujamos el borde
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        // Crear el campo de contraseña
        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };

        passwordField.setOpaque(false);
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setEchoChar('●');
        passwordField.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        // Agregar componentes al panel
        panel.add(passwordField, BorderLayout.CENTER);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Primero dibujamos el fondo
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2d.dispose();

                // Luego dibujamos el contenido (texto)
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
        // Eliminamos el borde compuesto ya que ahora manejamos el borde en paintBorder
        field.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Primero dibujamos el fondo
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2d.dispose();

                // Luego dibujamos el contenido (texto)
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
        field.setEchoChar('●');
        // Eliminamos el borde compuesto ya que ahora manejamos el borde en paintBorder
        field.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        return field;
    }

    private JButton createStyledButton(String text) {
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

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createHoverLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                label.setForeground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.WHITE);
            }
        });
        return label;
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("×") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(255, 0, 0));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(235, 0, 0));
                } else {
                    g2d.setColor(new Color(255, 255, 255, 50));
                }
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        return closeButton;
    }

    private void setNumericFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void handleLogin() {
        String documento = emailField.getText();
        String password = new String(passwordField.getPassword());

        Login login = new Login();
        if (login.verifyUser(documento, password) != null) {
            MainApplication mainGUI = new MainApplication();
            mainGUI.setVisible(true);
            this.dispose();
        } else {
            showErrorDialog("Credenciales incorrectas. Intente nuevamente.");
        }
    }

    private void verificarAdministrador() {
        // Crear un JDialog personalizado
        JDialog dialog = new JDialog(this, "Verificar Administrador", true);
        dialog.setSize(350, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        // Panel principal con el mismo degradado
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

                // Patrón de puntos
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < w; i += 20) {
                    for (int j = 0; j < h; j += 20) {
                        g2d.fillOval(i, j, 2, 2);
                    }
                }
            }
        };
        mainPanel.setLayout(null);

        // Hacer el diálogo draggable
        Point[] dragPoint = new Point[1];
        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragPoint[0] = e.getPoint();
            }
        });

        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                dialog.setLocation(current.x - dragPoint[0].x,
                        current.y - dragPoint[0].y);
            }
        });

        // Título
        JLabel titleLabel = new JLabel("Administrador");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(108, 30, 250, 30);
        mainPanel.add(titleLabel);

        // Icono de administrador
        JPanel adminIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Círculo blanco de fondo
                g2d.setColor(Color.WHITE);
                g2d.fillOval(0, 0, 60, 60);

                // Silueta del admin con "corbata"
                g2d.setColor(primaryColor);
                g2d.fillOval(20, 10, 20, 20); // Cabeza
                g2d.fillOval(10, 25, 40, 30); // Cuerpo
                int[] xPoints = {25, 35, 30};
                int[] yPoints = {35, 35, 50};
                g2d.fillPolygon(xPoints, yPoints, 3); // Corbata
            }
        };
        adminIcon.setOpaque(false);
        adminIcon.setBounds(145, 70, 60, 60);
        mainPanel.add(adminIcon);

        // Campo de documento
        JLabel docLabel = new JLabel("Documento");
        docLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        docLabel.setForeground(Color.WHITE);
        docLabel.setBounds(50, 150, 250, 30);
        mainPanel.add(docLabel);

        JTextField adminDocField = createStyledTextField();
        adminDocField.setBounds(50, 180, 250, 40);
        setNumericFilter(adminDocField);
        mainPanel.add(adminDocField);

        // Campo de contraseña
        JLabel passLabel = new JLabel("Contraseña");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50, 230, 250, 30);
        mainPanel.add(passLabel);

        JPasswordField adminPassField = createStyledPasswordField();
        adminPassField.setBounds(50, 260, 250, 40);
        mainPanel.add(adminPassField);

        // Botones
        JButton verifyButton = createStyledButton("Verificar");
        verifyButton.setBounds(50, 320, 120, 40);
        verifyButton.addActionListener(e -> {
            if (validateAdminCredentials(adminDocField.getText(),
                    new String(adminPassField.getPassword()))) {
                dialog.dispose();
                InterRegister registro = new InterRegister();
                registro.setVisible(true);
                this.dispose();
            } else {
                showErrorDialog("Credenciales de administrador incorrectas.");
            }
        });
        mainPanel.add(verifyButton);

        JButton cancelButton = createStyledButton("Cancelar");
        cancelButton.setBounds(180, 320, 120, 40);
        cancelButton.addActionListener(e -> dialog.dispose());
        mainPanel.add(cancelButton);

        // Agregar sombra al panel
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1, true)
        ));

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JDialog errorDialog = new JDialog(this, "Error", true);
        errorDialog.setSize(300, 150);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setUndecorated(true);

        JPanel errorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(180, 0, 0),
                        0, getHeight(), new Color(120, 0, 0));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        errorPanel.setLayout(null);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBounds(20, 20, 260, 60);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorPanel.add(messageLabel);

        JButton okButton = createStyledButton("Aceptar");
        okButton.setBounds(100, 90, 100, 35);
        okButton.addActionListener(e -> errorDialog.dispose());
        errorPanel.add(okButton);

        errorDialog.add(errorPanel);
        errorDialog.setVisible(true);
    }

    private boolean validateAdminCredentials(String document, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("ModelosBase/Login_Archivo/Admin.txt"))) {
            String line;
            if ((line = br.readLine()) != null) {
                String[] credentials = line.split("@@");
                String adminDoc = credentials[2].trim();
                String adminPass = credentials[3].trim();
                return adminDoc.equals(document) && adminPass.equals(password);
            }
        } catch (IOException e) {
            showErrorDialog("Error al leer el archivo de credenciales.");
        }
        return false;
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
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }
    }
}