package Proyecto.EstructurasDatos;

public class ListaEnlazada<T> {
    private Nodo<T> cabeza;
    private int tamanio = 0;

    public void insertar(T elemento) {
        if (!contiene(elemento)) {
            Nodo<T> nuevoNodo = new Nodo<>(elemento);
            if (cabeza == null) {
                cabeza = nuevoNodo;
            } else {
                Nodo<T> actual = cabeza;
                while (actual.getSiguienteNodo() != null) {
                    actual = actual.getSiguienteNodo();
                }
                actual.setSiguienteNodo(nuevoNodo);
            }
            tamanio++;
        } else {
            System.out.println("El elemento " + elemento + " ya está en la lista. No se añadirá.");
        }
    }

    public void insertarDespuesDe(T elementoExistente, T nuevoElemento) {
        if (!contiene(nuevoElemento)) {
            Nodo<T> actual = cabeza;
            while (actual != null && !actual.getValorNodo().equals(elementoExistente)) {
                actual = actual.getSiguienteNodo();
            }
            if (actual != null) {
                Nodo<T> nuevoNodo = new Nodo<>(nuevoElemento);
                nuevoNodo.setSiguienteNodo(actual.getSiguienteNodo());
                actual.setSiguienteNodo(nuevoNodo);
                tamanio++;
            } else {
                System.out.println("El elemento existente no se encuentra en la lista.");
            }
        } else {
            System.out.println("El elemento " + nuevoElemento + " ya está en la lista. No se añadirá.");
        }
    }

    public boolean contiene(T elemento) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.getValorNodo().equals(elemento)) {
                return true;
            }
            actual = actual.getSiguienteNodo();
        }
        return false;
    }

    public Nodo<T> getCabeza() {
        return cabeza;
    }

    public void setCabeza(Nodo<T> cabeza) {
        this.cabeza = cabeza;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public void eliminar(T elemento) {
        if (cabeza == null) {
            return;
        }
        if (cabeza.getValorNodo().equals(elemento)) {
            cabeza = cabeza.getSiguienteNodo();
            tamanio--;
            return;
        }
        Nodo<T> actual = cabeza;
        while (actual.getSiguienteNodo() != null && !actual.getSiguienteNodo().getValorNodo().equals(elemento)) {
            actual = actual.getSiguienteNodo();
        }
        if (actual.getSiguienteNodo() != null) {
            actual.setSiguienteNodo(actual.getSiguienteNodo().getSiguienteNodo());
            tamanio--;
        }
    }
    public T getElementoEnPosicion(int posicion) {
        if (posicion < 0 || posicion >= tamanio) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion);
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguienteNodo();
        }
        return actual.getValorNodo();
    }
}
