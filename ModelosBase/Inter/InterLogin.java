package Proyecto.ModelosBase.Inter;

import Proyecto.ModelosBase.Login;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class InterLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;

    public InterLogin() {
        // Configuración básica de la ventana
        setTitle("Inicio de sesión");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con degradado púrpura
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(147, 112, 219); // Color morado
                Color color2 = new Color(138, 43, 226);  // Color morado
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);

        // Título
        JLabel titleLabel = new JLabel("Inicio de sesión");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(130, 30, 200, 40);
        mainPanel.add(titleLabel);

        // Campo de email
        JLabel emailLabel = new JLabel("Documento");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(50, 100, 100, 30);
        mainPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(50, 130, 300, 40);
        emailField.setOpaque(false); // Hacer el fondo transparente
        emailField.setForeground(Color.WHITE); // Cambiar el color del texto a blanco
        emailField.setBorder(BorderFactory.createCompoundBorder(
                new BottomBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10))); // Solo borde inferior
        setNumericFilter(emailField); // Aplica el filtro para solo números
        mainPanel.add(emailField);

        // Campo de contraseña
        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 180, 100, 30);
        mainPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 210, 300, 40);
        passwordField.setOpaque(false); // Hacer el fondo transparente
        passwordField.setForeground(Color.WHITE); // Cambiar el color del texto a blanco
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new BottomBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10))); // Solo borde inferior
        mainPanel.add(passwordField);

        // Checkbox "Recordarme"
        rememberMe = new JCheckBox("Recordarme");
        rememberMe.setBounds(50, 260, 120, 30);
        rememberMe.setForeground(Color.WHITE);
        rememberMe.setOpaque(false);
        mainPanel.add(rememberMe);

        // Link "Olvidó su contraseña?"
        JLabel forgetPassword = new JLabel("Olvidó su contraseña?");
        forgetPassword.setForeground(Color.WHITE);
        forgetPassword.setBounds(220, 260, 130, 30);
        forgetPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(forgetPassword);

        // Botón de inicio de sesión
        JButton loginButton = new JButton("Iniciar sesión");
        loginButton.setBounds(50, 310, 300, 40);
        loginButton.setBackground(new Color(147, 112, 219)); // Color sólido morado
        loginButton.setForeground(Color.BLACK); // Color del texto negro
        loginButton.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita y tamaño 16
        loginButton.setBorder(new RoundedBorder(20));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> handleLogin()); // Método para manejar el inicio de sesión
        mainPanel.add(loginButton);

        // Texto y link de registro
        JLabel noAccountLabel = new JLabel("No tengo cuenta");
        noAccountLabel.setForeground(Color.WHITE);
        noAccountLabel.setBounds(90, 360, 150, 30);
        mainPanel.add(noAccountLabel);

        JLabel registerLink = new JLabel("registrarse");
        registerLink.setForeground(Color.WHITE);
        registerLink.setBounds(240, 360, 100, 30);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(registerLink);

        // Botón de cierre (X)
        JButton closeButton = new JButton("×");
        closeButton.setBounds(350, 10, 30, 30);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> System.exit(0));
        mainPanel.add(closeButton);

        add(mainPanel);
        setUndecorated(true); // Elimina la decoración de la ventana
    }

    // Método para establecer un filtro que solo permita números
    private void setNumericFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
            }
        });
    }

    // Método para manejar el inicio de sesión
    private void handleLogin() {
        String documento = emailField.getText(); // Captura el documento
        String password = new String(passwordField.getPassword()); // Captura la contraseña

        // Verifica el usuario
        String userType = Login.verifyUser(documento, password);

        // Maneja el resultado de la verificación
        if (userType != null) {
            // El usuario es válido
            JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso como " + userType, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Aquí puedes redirigir al usuario a la interfaz correspondiente según el tipo de usuario
        } else {
            // El usuario no es válido
            JOptionPane.showMessageDialog(this, "Documento o clave incorrectos. Intente de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clase para crear bordes inferiores
    private static class BottomBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.drawLine(x, y + height - 1, x + width - 1, y + height - 1); // Dibuja solo el borde inferior
            g2.dispose();
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
