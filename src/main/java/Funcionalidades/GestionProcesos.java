package Funcionalidades;

import EstructurasDatos.Mapa;
import Interfaz.GestorNotificacionesSwing;
import ModelosBase.Actividad;
import Notificaciones.MonitorNotificaciones;
import ModelosBase.Proceso;
import java.util.UUID;

public class GestionProcesos {
    private Mapa<UUID, Proceso> procesos;
    private final MonitorNotificaciones monitorNotificaciones;
    private final GestionNotificaciones gestionNotificaciones;
    private final GestorNotificacionesSwing gestorNotificacionesUI;

    public GestionProcesos(Mapa<UUID, Proceso> procesos) {
        this.procesos = procesos;
        this.monitorNotificaciones = MonitorNotificaciones.getInstance();
        this.gestionNotificaciones = GestionNotificaciones.getInstance();
        this.gestorNotificacionesUI = GestorNotificacionesSwing.getInstance();
    }

    public Proceso crearProceso(String nombre) {
        Proceso proceso = new Proceso(nombre);
        procesos.put(proceso.getId(), proceso);
        gestionNotificaciones.notificarProcesoIniciado(proceso);
        return proceso;
    }

    public void agregarActividad(UUID procesoId, Actividad actividad) {
        Proceso proceso = procesos.get(procesoId);
        if (proceso != null) {
            proceso.agregarActividad(actividad);
        }
    }

    public Proceso buscarProceso(UUID id) {
        return procesos.get(id);
    }

    public Mapa<UUID, Proceso> getProcesos() {
        return procesos;
    }

    public void setProcesos(Mapa<UUID, Proceso> procesos) {
        this.procesos = procesos;
    }

    public void eliminarProceso(UUID procesoId) {
        procesos.remove(procesoId);
    }

}
