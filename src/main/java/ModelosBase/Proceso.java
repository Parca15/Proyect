package ModelosBase;

import EstructurasDatos.ListaEnlazada;

import java.time.LocalDateTime;
import java.util.UUID;

public class Proceso {
    private String nombre;
    private UUID id;
    private ListaEnlazada<Actividad> actividades;
    private LocalDateTime fechaInicio;

    public Proceso( String nombre) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.actividades = new ListaEnlazada<>();
        this.fechaInicio = LocalDateTime.now();
    }
    public void agregarActividad(Actividad actividad) {
        actividades.insertar(actividad);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ListaEnlazada<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(ListaEnlazada<Actividad> actividades) {
        this.actividades = actividades;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

}
