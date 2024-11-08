package Interfaz;

import Notificaciones.Notificacion;
import Notificaciones.PrioridadNotificacion;

import javax.swing.*;
import java.awt.*;

public class VentanaNotificacion extends JDialog {
    public VentanaNotificacion(Notificacion notificacion, int anchoVentanaActual) {
        super((Frame) null, "Notificación", false);
        setUndecorated(true);

        // Panel principal con borde
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getPriorityColor(notificacion.getPrioridad()), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Título
        JLabel titleLabel = new JLabel(notificacion.getTitulo());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(titleLabel);

        // Mensaje
        JLabel messageLabel = new JLabel("<html><body style='width: 200px'>" +
                notificacion.getMensaje() + "</body></html>");
        panel.add(Box.createVerticalStrut(5));
        panel.add(messageLabel);

        // Botón de cerrar
        JButton closeButton = new JButton("×");
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> dispose());
        panel.add(Box.createVerticalStrut(5));
        panel.add(closeButton);

        add(panel);
        pack();

        // Ubicar la ventana
        posicionarVentana(anchoVentanaActual);

        // Timer para cerrar automáticamente
        Timer timer = new Timer(5000, e -> dispose());
        timer.setRepeats(false);
        timer.start();

        setVisible(true);
    }

    private Color getPriorityColor(PrioridadNotificacion prioridad) {
        return switch (prioridad) {
            case ALTA -> Color.RED;
            case MEDIA -> Color.ORANGE;
            case BAJA -> Color.BLUE;
        };
    }

    private void posicionarVentana(int anchoVentanaActual) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - getWidth() - 20;
        int y = screenSize.height - getHeight() - 20;
        setLocation(x, y);
    }
}