package Proyecto.Funcionalidades;

import Proyecto.ModelosBase.Actividad;
import Proyecto.ModelosBase.Proceso;

import java.util.Map;
import java.util.UUID;

public class GestionProcesos {
    private Map<UUID, Proceso> procesos;

    public GestionProcesos(Map<UUID, Proceso> procesos) {
        this.procesos = procesos;
    }

    public Proceso crearProceso(String nombre) {
        Proceso proceso = new Proceso(nombre);
        procesos.put(proceso.getId(), proceso);
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

    public Map<UUID, Proceso> getProcesos() {
        return procesos;
    }

    public void setProcesos(Map<UUID, Proceso> procesos) {
        this.procesos = procesos;
    }

    public void eliminarProceso(UUID procesoId) {
        procesos.remove(procesoId);
    }

}
