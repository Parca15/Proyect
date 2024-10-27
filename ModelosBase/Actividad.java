package Proyecto.ModelosBase;

import Proyecto.EstructurasDatos.Cola;
import java.time.LocalDateTime;

public class Actividad {
    private String nombre;
    private String descripcion;
    private boolean obligatoria;
    private Cola<Tarea> tareas;
    private Actividad anterior;
    private Actividad siguiente;
    private final LocalDateTime fechaInicio;

    public Actividad(String nombre, String descripcion, boolean obligatoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.obligatoria = obligatoria;
        this.tareas = new Cola<>();
        this.fechaInicio = LocalDateTime.now();
    }

    public void agregarTarea(Tarea tarea){
        tareas.encolar(tarea);
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public Actividad getAnterior() {
        return anterior;
    }

    public void setAnterior(Actividad anterior) {
        this.anterior = anterior;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isObligatoria() {
        return obligatoria;
    }

    public void setObligatoria(boolean obligatoria) {
        this.obligatoria = obligatoria;
    }

    public Actividad getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Actividad siguiente) {
        this.siguiente = siguiente;
    }

    public Cola<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(Cola<Tarea> tareas) {
        this.tareas = tareas;
    }
}
