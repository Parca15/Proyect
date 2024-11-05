package Interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DetalleEstadisticaPanel extends JPanel {
    private final JTextArea detalleArea;
    private final JScrollPane scrollPane;

    public DetalleEstadisticaPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(139, 92, 246));
        setBorder(BorderFactory.createLineBorder(new Color(167, 139, 250), 2));

        detalleArea = new JTextArea();
        detalleArea.setEditable(false);
        detalleArea.setWrapStyleWord(true);
        detalleArea.setLineWrap(true);
        detalleArea.setBackground(new Color(139, 92, 246));
        detalleArea.setForeground(Color.WHITE);
        detalleArea.setFont(new Font("Arial", Font.PLAIN, 12));
        detalleArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Hacer que el JTextArea propague los eventos de clic
        detalleArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Propagar el evento al padre
                Container parent = getParent();
                if (parent != null) {
                    parent.dispatchEvent(SwingUtilities.convertMouseEvent(detalleArea, e, parent));
                }
            }
        });

        // Agregar JScrollPane para manejar contenido largo
        scrollPane = new JScrollPane(detalleArea);
        scrollPane.setBackground(new Color(139, 92, 246));
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(139, 92, 246));

        // Hacer que el JScrollPane también propague los eventos de clic
        scrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Container parent = getParent();
                if (parent != null) {
                    parent.dispatchEvent(SwingUtilities.convertMouseEvent(scrollPane, e, parent));
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Hacer que el panel también propague los eventos de clic
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Container parent = getParent();
                if (parent != null) {
                    parent.dispatchEvent(SwingUtilities.convertMouseEvent(DetalleEstadisticaPanel.this, e, parent));
                }
            }
        });
    }

    public void setDetalle(String detalle) {
        detalleArea.setText(detalle);
        // Volver al inicio del scroll
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
}