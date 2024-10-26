package Proyecto.ModelosBase;

import java.time.LocalDateTime;

public class Tarea {
    private String descripcion;
    private boolean obligatoria;
    private int duracion;
    private LocalDateTime fechaCreacion;

    public Tarea(String descripcion, int duracion, boolean obligatoria) {
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.obligatoria = obligatoria;
        this.fechaCreacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public boolean isObligatoria() {
        return obligatoria;
    }

    public void setObligatoria(boolean obligatoria) {
        this.obligatoria = obligatoria;
    }
}
