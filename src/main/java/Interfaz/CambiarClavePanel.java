package Interfaz;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ArrayList;

public class CambiarClavePanel extends JPanel {
    private JTextField documentField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private Color primaryColor = new Color(147, 112, 219);
    private Color secondaryColor = new Color(138, 43, 226);
    private Color hoverColor = new Color(123, 104, 238);
    private Point initialClick;

    public CambiarClavePanel() {
        setLayout(null);
        setBackground(primaryColor);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        // Hacer el panel draggable
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = thisX + e.getX() - initialClick.x;
                int yMoved = thisY + e.getY() - initialClick.y;

                setLocation(xMoved, yMoved);
            }
        });

        // Título con sombra
        JLabel titleLabel = new JLabel("Cambiar contraseña");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 20, 300, 40);
        add(titleLabel);

        // Campo de documento
        JLabel docLabel = new JLabel("Documento");
        docLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        docLabel.setForeground(Color.WHITE);
        docLabel.setBounds(50, 80, 200, 30);
        add(docLabel);

        documentField = createStyledTextField();
        documentField.setBounds(50, 110, 300, 40);
        setNumericFilter(documentField);
        add(documentField);

        // Campo de contraseña actual
        JLabel currentPassLabel = new JLabel("Contraseña actual");
        currentPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentPassLabel.setForeground(Color.WHITE);
        currentPassLabel.setBounds(50, 160, 200, 30);
        add(currentPassLabel);

        currentPasswordField = createStyledPasswordField();
        currentPasswordField.setBounds(50, 190, 300, 40);
        add(currentPasswordField);

        // Campo de nueva contraseña
        JLabel newPassLabel = new JLabel("Nueva contraseña");
        newPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPassLabel.setForeground(Color.WHITE);
        newPassLabel.setBounds(50, 240, 200, 30);
        add(newPassLabel);

        newPasswordField = createStyledPasswordField();
        newPasswordField.setBounds(50, 270, 300, 40);
        add(newPasswordField);

        // Botón de cambiar contraseña
        JButton changePasswordButton = createStyledButton("Cambiar contraseña");
        changePasswordButton.setBounds(50, 330, 300, 40);
        changePasswordButton.addActionListener(e -> handleChangePassword());
        add(changePasswordButton);

        JButton botonVolver = createStyledButton("Volver");
        botonVolver.setBounds(50, 380, 300, 40);
        botonVolver.addActionListener(e -> irAInterfazPrincipal());
        add(botonVolver);
    }

    private void irAInterfazPrincipal() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose(); // Cierra la ventana actual

        InterLogin interfazPrincipal = new InterLogin();
        interfazPrincipal.setVisible(true);
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
        field.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Cambiar el color del botón
                if (getModel().isPressed()) {
                    g2d.setColor(secondaryColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(secondaryColor);
                } else {
                    g2d.setColor(secondaryColor);
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

    private void setNumericFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (string.matches("[0-9]*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                if (text.matches("[0-9]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    public void handleChangePassword() {
        String document = documentField.getText();
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());

        // Validaciones básicas
        if (document.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que la nueva contraseña no sea igual a la actual
        if (currentPassword.equals(newPassword)) {
            JOptionPane.showMessageDialog(this,
                    "La nueva contraseña debe ser diferente a la actual",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Intentar actualizar en ambos archivos
            File adminFile = new File("src/main/resources/Login_Archivo/Admin");
            File userFile = new File("src/main/resources/Login_Archivo/Usuarios");

            boolean updatedInAdmin = false;
            boolean updatedInUsers = false;

            if (adminFile.exists()) {
                updatedInAdmin = findAndUpdateUser(adminFile, document, currentPassword, newPassword);
            }

            if (userFile.exists()) {
                updatedInUsers = findAndUpdateUser(userFile, document, currentPassword, newPassword);
            }

            // Verificar si se actualizó en algún archivo
            if (updatedInAdmin || updatedInUsers) {
                JOptionPane.showMessageDialog(this,
                        "Contraseña actualizada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                // Limpiar campos y volver a la pantalla de login
                documentField.setText("");
                currentPasswordField.setText("");
                newPasswordField.setText("");
                irAInterfazPrincipal();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Documento o contraseña actual incorrectos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar la contraseña: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método auxiliar para encontrar y actualizar al usuario
    private boolean findAndUpdateUser(File file, String document, String currentPassword, String newPassword) throws IOException {
        List<String> lines = new ArrayList<>(Files.readAllLines(file.toPath()));
        boolean updated = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] userData = lines.get(i).split("@@");
            if (userData.length >= 4 && userData[2].equals(document) && userData[3].equals(currentPassword)) {
                userData[3] = newPassword;
                lines.set(i, String.join("@@", userData));
                updated = true;
                break;
            }
        }

        if (updated) {
            Files.write(file.toPath(), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        return updated;
    }

}
