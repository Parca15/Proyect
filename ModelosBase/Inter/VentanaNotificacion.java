package Proyecto.ModelosBase.Inter;

import Proyecto.ModelosBase.Notificaciones.Notificacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaNotificacion extends JDialog {
    private static final int TIEMPO_MOSTRAR = 5000; // 5 segundos
    private static final int TIEMPO_ANIMACION = 500; // 0.5 segundos
    private Timer temporizadorOcultar;
    private Timer temporizadorAnimacion;
    private int posicionYObjetivo;
    private int posicionYActual;

    public VentanaNotificacion(Notificacion notificacion, int anchoVentana) {
        setUndecorated(true);
        setAlwaysOnTop(true);

        PanelNotificacion panelNotificacion = new PanelNotificacion(notificacion);
        add(panelNotificacion);

        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = anchoVentana - getWidth() - 20;
        posicionYObjetivo = screenSize.height - getHeight() - 50;
        posicionYActual = screenSize.height;
        setLocation(x, posicionYActual);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        configurarTemporizadores();

        setVisible(true);
        temporizadorAnimacion.start();
    }

    private void configurarTemporizadores() {
        temporizadorOcultar = new Timer(TIEMPO_MOSTRAR, e -> {
            temporizadorAnimacion.restart();
            posicionYObjetivo = Toolkit.getDefaultToolkit().getScreenSize().height;
        });
        temporizadorOcultar.setRepeats(false);

        ActionListener accionAnimacion = e -> {
            if (posicionYActual < posicionYObjetivo) {
                posicionYActual += 10;
                if (posicionYActual >= posicionYObjetivo) {
                    posicionYActual = posicionYObjetivo;
                    temporizadorAnimacion.stop();
                    if (posicionYObjetivo > getY()) {
                        dispose();
                    }
                }
            } else {
                posicionYActual -= 10;
                if (posicionYActual <= posicionYObjetivo) {
                    posicionYActual = posicionYObjetivo;
                    temporizadorAnimacion.stop();
                    temporizadorOcultar.start();
                }
            }
            setLocation(getX(), posicionYActual);
        };

        temporizadorAnimacion = new Timer(TIEMPO_ANIMACION / 50, accionAnimacion);
    }
}