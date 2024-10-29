package Funcionalidades;

import EstructurasDatos.Nodo;
import ModelosBase.Actividad;
import ModelosBase.Proceso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GestionActividades {
    GestionProcesos gestionProcesos;
    private Actividad ultimaActividad;

    public GestionActividades(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
        this.ultimaActividad = null;
    }


    public void agregarActividad(UUID idProceso, String nombre, String descripcion, boolean obligatoria) {
        Actividad nuevaActividad = new Actividad(nombre, descripcion, obligatoria);
        try {
            if (idProceso == null) {
                System.out.println("Error: El ID del proceso no puede ser null.");
                return;
            }

            Proceso proceso = gestionProcesos.buscarProceso(idProceso);
            if (proceso == null) {
                System.out.println("Error: No se encontró el proceso con ID: " + idProceso);
                return;
            }

            if (actividadExiste(idProceso, nombre)) {
                System.out.println("Error: Ya existe una actividad con el nombre '" + nombre + "'.");
                return;
            }


            gestionProcesos.agregarActividad(idProceso, nuevaActividad);
            System.out.println("Actividad '" + nombre + "' agregada exitosamente al proceso.");

        } catch (Exception e) {
            System.out.println("Error al agregar la actividad: " + e.getMessage());
        }
        ultimaActividad = nuevaActividad;
    }

    public void insertarActividadDespuesDe(UUID id,String nombreExistente, String nombre, String descripcion, boolean obligatoria) {
        if (actividadExiste(id,nombre)) {
            System.out.println("Error: Ya existe una actividad con el nombre '" + nombre + "'.");
            return;
        }
        Actividad nuevaActividad = new Actividad(nombre, descripcion, obligatoria);
        Actividad actividadExistente = buscarActividadPorNombre(id,nombreExistente);
        if (actividadExistente != null) {
            gestionProcesos.buscarProceso(id).getActividades().insertarDespuesDe(actividadExistente, nuevaActividad);
            ultimaActividad = nuevaActividad;
        } else {
            System.out.println("Error: La actividad '" + nombreExistente + "' no existe.");
        }
    }

    public void insertarActividadAlFinal(UUID id,String nombre, String descripcion, boolean obligatoria) {
        if (actividadExiste(id, nombre)) {
            System.out.println("Error: Ya existe una actividad con el nombre '" + nombre + "'.");
            return;
        }
        Actividad nuevaActividad = new Actividad(nombre, descripcion, obligatoria);
        gestionProcesos.buscarProceso(id).getActividades().insertar(nuevaActividad);
        ultimaActividad = nuevaActividad;
    }

    public void insertarActividadDespuesDeUltimaCreada(UUID id,String nombre, String descripcion, boolean obligatoria) {
        if (actividadExiste(id, nombre)) {
            System.out.println("Error: Ya existe una actividad con el nombre '" + nombre + "'.");
            return;
        }
        Actividad nuevaActividad = new Actividad(nombre, descripcion, obligatoria);
        if (ultimaActividad == null) {
            gestionProcesos.buscarProceso(id).getActividades().insertar(nuevaActividad);
        } else {
            gestionProcesos.buscarProceso(id).getActividades().insertarDespuesDe(ultimaActividad,nuevaActividad);
        }
        ultimaActividad = nuevaActividad;
    }

    private boolean actividadExiste(UUID id, String nombre) {
        Nodo<Actividad> actual = gestionProcesos.buscarProceso(id).getActividades().getCabeza();
        while (actual != null) {
            if (actual.getValorNodo().getNombre().equals(nombre)) {
                return true;
            }
            actual = actual.getSiguienteNodo();
        }
        return false;
    }

    private Actividad buscarActividadPorNombre(UUID id,String nombre) {
        Nodo<Actividad> actual = gestionProcesos.buscarProceso(id).getActividades().getCabeza();
        while (actual != null) {
            if (actual.getValorNodo().getNombre().equals(nombre)) {
                return actual.getValorNodo();
            }
            actual = actual.getSiguienteNodo();
        }
        return null;
    }

    public void intercambiarActividades(UUID id, String nombre1, String nombre2, boolean selected) {
        if (nombre1.equals(nombre2)) {
            System.out.println("Error: Los nombres son iguales. No se puede realizar el intercambio.");
            return;
        }
        Actividad actividad1 = buscarActividadPorNombre(id,nombre1);
        Actividad actividad2 = buscarActividadPorNombre(id, nombre2);
        if (actividad1 == null || actividad2 == null) {
            System.out.println("Error: Una o ambas actividades no existen.");
            return;
        }
        String tempNombre = actividad1.getNombre();
        String tempDescripcion = actividad1.getDescripcion();
        boolean tempObligatoria = actividad1.isObligatoria();
        actividad1.setNombre(actividad2.getNombre());
        actividad1.setDescripcion(actividad2.getDescripcion());
        actividad1.setObligatoria(actividad2.isObligatoria());
        actividad2.setNombre(tempNombre);
        actividad2.setDescripcion(tempDescripcion);
        actividad2.setObligatoria(tempObligatoria);
    }

    public List<String> obtenerNombresActividades(UUID id) {
        List<String> nombres = new ArrayList<>();
        Nodo<Actividad> actual = gestionProcesos.buscarProceso(id).getActividades().getCabeza();

        while (actual != null) {
            nombres.add(actual.getValorNodo().getNombre());
            actual = actual.getSiguienteNodo();
        }

        return nombres;
    }


    public Actividad getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(Actividad ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

}

