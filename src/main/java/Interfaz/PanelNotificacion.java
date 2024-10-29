package Interfaz;

import ModelosBase.Notificaciones.Notificacion;
import ModelosBase.Notificaciones.PrioridadNotificacion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

public class PanelNotificacion extends JPanel {
    private static final Color COLOR_ALTA = new Color(255, 200, 200);
    private static final Color COLOR_MEDIA = new Color(255, 255, 200);
    private static final Color COLOR_BAJA = new Color(200, 255, 200);
    private static final int ALTURA_PANEL = 100;
    private static final int ANCHO_PANEL = 300;

    public PanelNotificacion(Notificacion notificacion) {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setPreferredSize(new Dimension(ANCHO_PANEL, ALTURA_PANEL));

        setBackground(getColorPrioridad(notificacion.getPrioridad()));

        JLabel etiquetaTitulo = new JLabel(notificacion.getTitulo());
        etiquetaTitulo.setFont(etiquetaTitulo.getFont().deriveFont(Font.BOLD));

        JTextArea areaMensaje = new JTextArea(notificacion.getMensaje());
        areaMensaje.setWrapStyleWord(true);
        areaMensaje.setLineWrap(true);
        areaMensaje.setOpaque(false);
        areaMensaje.setEditable(false);

        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        JLabel etiquetaTiempo = new JLabel(notificacion.getFechaCreacion().format(formateador));
        etiquetaTiempo.setFont(etiquetaTiempo.getFont().deriveFont(Font.ITALIC, 10f));

        add(etiquetaTitulo, BorderLayout.NORTH);
        add(areaMensaje, BorderLayout.CENTER);
        add(etiquetaTiempo, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(getColorPrioridad(notificacion.getPrioridad()).brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(getColorPrioridad(notificacion.getPrioridad()));
            }
        });
    }

    private Color getColorPrioridad(PrioridadNotificacion prioridad) {
        return switch (prioridad) {
            case ALTA -> COLOR_ALTA;
            case MEDIA -> COLOR_MEDIA;
            default -> COLOR_BAJA;
        };
    }
}