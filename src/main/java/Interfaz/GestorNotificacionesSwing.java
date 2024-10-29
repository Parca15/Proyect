package Interfaz;

import ModelosBase.Notificaciones.Notificacion;
import Funcionalidades.GestionNotificaciones;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GestorNotificacionesSwing {
    private static GestorNotificacionesSwing instancia;
    private final List<VentanaNotificacion> notificacionesActivas;
    private final int anchoVentanaActual;

    private GestorNotificacionesSwing() {
        this.notificacionesActivas = new ArrayList<>();
        this.anchoVentanaActual = Toolkit.getDefaultToolkit().getScreenSize().width;

        GestionNotificaciones.getInstance().setNotificationHandler(this::mostrarNotificacion);
    }

    public static GestorNotificacionesSwing getInstance() {
        if (instancia == null) {
            instancia = new GestorNotificacionesSwing();
        }
        return instancia;
    }

    public void mostrarNotificacion(Notificacion notificacion) {
        SwingUtilities.invokeLater(() -> {
            limpiarNotificaciones();
            VentanaNotificacion ventana = new VentanaNotificacion(notificacion, anchoVentanaActual);
            notificacionesActivas.add(ventana);
        });
    }

    private void limpiarNotificaciones() {
        notificacionesActivas.removeIf(ventana -> !ventana.isDisplayable());
    }
}