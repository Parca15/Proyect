package ModelosBase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tarea implements Cloneable {
    private String descripcion;
    private boolean obligatoria;
    private int duracion; // En minutos
    private LocalDateTime fechaCreacion;
    private List<Tarea> subtareas; // Lista de subtareas

    // Constructor original
    public Tarea(String descripcion, int duracion, boolean obligatoria) {
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.obligatoria = obligatoria;
        this.fechaCreacion = LocalDateTime.now();
        this.subtareas = new ArrayList<>();
    }

    // Constructor de copia
    public Tarea(Tarea otraTarea) {
        this.descripcion = otraTarea.descripcion;
        this.duracion = otraTarea.duracion;
        this.obligatoria = otraTarea.obligatoria;
        this.fechaCreacion = LocalDateTime.of(
                otraTarea.fechaCreacion.getYear(),
                otraTarea.fechaCreacion.getMonth(),
                otraTarea.fechaCreacion.getDayOfMonth(),
                otraTarea.fechaCreacion.getHour(),
                otraTarea.fechaCreacion.getMinute()
        );
        this.subtareas = new ArrayList<>();
        // Copiar subtareas si existen
        for (Tarea subtarea : otraTarea.getSubtareas()) {
            this.subtareas.add(new Tarea(subtarea));
        }
    }

    // Getter y Setter para subtareas
    public List<Tarea> getSubtareas() {
        return new ArrayList<>(subtareas); // Retorna una copia de la lista
    }

    public void setSubtareas(List<Tarea> subtareas) {
        this.subtareas = new ArrayList<>(subtareas); // Crea una copia de la lista
    }

    // Métodos para manejar subtareas
    public void agregarSubtarea(Tarea subtarea) {
        this.subtareas.add(new Tarea(subtarea)); // Agrega una copia de la subtarea
    }

    public void removerSubtarea(int index) {
        if (index >= 0 && index < subtareas.size()) {
            subtareas.remove(index);
        }
    }

    // Implementación del método clone()
    @Override
    public Tarea clone() {
        try {
            Tarea clonada = (Tarea) super.clone();
            // Realizar una copia profunda de los objetos mutables
            clonada.fechaCreacion = LocalDateTime.of(
                    this.fechaCreacion.getYear(),
                    this.fechaCreacion.getMonth(),
                    this.fechaCreacion.getDayOfMonth(),
                    this.fechaCreacion.getHour(),
                    this.fechaCreacion.getMinute()
            );
            clonada.subtareas = new ArrayList<>();
            for (Tarea subtarea : this.subtareas) {
                clonada.subtareas.add(subtarea.clone());
            }
            return clonada;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error al clonar la tarea", e);
        }
    }

    // Getters y setters existentes
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

    // Método toString para debugging
    @Override
    public String toString() {
        return "Tarea{" +
                "descripcion='" + descripcion + '\'' +
                ", obligatoria=" + obligatoria +
                ", duracion=" + duracion +
                ", fechaCreacion=" + fechaCreacion +
                ", subtareas=" + subtareas.size() +
                '}';
    }
}