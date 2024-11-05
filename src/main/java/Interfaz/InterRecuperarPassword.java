package Interfaz;

import ModelosBase.RecuperarPassword;
import javax.swing.*;
import java.awt.*;

public class InterRecuperarPassword extends javax.swing.JFrame {
    private JTextField txtCorreo;
    private JTextField txtDocumento;
    private JButton btnRecuperar;
    private JButton btnCancelar;

    // Colors matching TareaPanel
    private final Color primaryColor = new Color(147, 112, 219);
    private final Color secondaryColor = new Color(138, 43, 226);
    private final Color hoverColor = new Color(123, 104, 238);

    public InterRecuperarPassword() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Recuperar Contraseña");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setPreferredSize(new Dimension(400, 300));

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Create gradient background
                float[] dist = {0.0f, 0.5f, 1.0f};
                Color[] colors = {primaryColor, new Color(142, 68, 223), secondaryColor};
                LinearGradientPaint gp = new LinearGradientPaint(0, 0, 0, h, dist, colors);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Decorative dots
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < w; i += 20) {
                    for (int j = 0; j < h; j += 20) {
                        g2d.fillOval(i, j, 2, 2);
                    }
                }
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        // Create components with matching style
        JLabel lblCorreo = createStyledLabel("Correo electrónico:");
        JLabel lblDocumento = createStyledLabel("Documento:");
        txtCorreo = createStyledTextField();
        txtDocumento = createStyledTextField();
        btnRecuperar = createStyledButton("Recuperar");
        btnCancelar = createStyledButton("Cancelar");

        // Layout components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(lblCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(txtCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(lblDocumento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(txtDocumento, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnRecuperar);
        buttonPanel.add(Box.createHorizontalStrut(12));
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.insets = new Insets(20, 15, 10, 15);
        mainPanel.add(buttonPanel, gbc);

        // Events
        btnRecuperar.addActionListener(e -> recuperarPassword());
        btnCancelar.addActionListener(e -> dispose());

        add(mainPanel);
        pack();
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20) {
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
        field.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return field;
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));

        return button;
    }

    private void recuperarPassword() {
        String correo = txtCorreo.getText().trim();
        String documento = txtDocumento.getText().trim();

        if (correo.isEmpty() || documento.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;
        }

        boolean resultado = RecuperarPassword.enviarPasswordPorCorreo(correo, documento);

        if (resultado) {
            mostrarExito("Se ha enviado la contraseña a su correo electrónico");
            dispose();
        } else {
            mostrarError("No se encontró una cuenta con esos datos");
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}