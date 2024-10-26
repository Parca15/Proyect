package Proyecto.ModelosBase;

import Proyecto.EstructurasDatos.ListaEnlazada;

import java.util.UUID;

public class Proceso {
    private String nombre;
    private UUID id;
    private ListaEnlazada<Actividad> actividades;

    public Proceso( String nombre) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.actividades = new ListaEnlazada<>();
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
}