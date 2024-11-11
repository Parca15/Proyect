package EstructurasDatos;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Mapa<T, U> implements Iterable<U> {
    private ListaEnlazada<T> llaves;
    private ListaEnlazada<U> valores;
    private int tamanio;

    public Mapa() {
        llaves = new ListaEnlazada<>();
        valores = new ListaEnlazada<>();
        tamanio = 0;
    }

    public void put(T llave, U valor) {
        if (!contiene(llave)) {
            llaves.insertar(llave);
            valores.insertar(valor);
            tamanio++;
        } else {
            System.out.println("La llave " + llave + " ya está en el mapa. No se añadirá.");
        }
    }

    public U get(T llave) {
        if (contiene(llave)) {
            int indice = llaves.indiceDe(llave);
            return valores.get(indice);
        } else {
            System.out.println("La llave " + llave + " no está en el mapa.");
            return null;
        }
    }

    public void remove(T llave) {
        if (contiene(llave)) {
            int indice = llaves.indiceDe(llave);
            llaves.eliminarEn(indice);
            valores.eliminarEn(indice);
            tamanio--;
        } else {
            System.out.println("La llave " + llave + " no está en el mapa.");
        }
    }

    private boolean contiene(T llave) {
        return llaves.contiene(llave);
    }

    @Override
    public Iterator<U> iterator() {
        return new MapaIterator<>(valores);
    }

    public int size() {
        return tamanio;
    }

    private static class MapaIterator<T> implements Iterator<T> {
        private ListaEnlazada<T> valores;
        private int indiceActual;

        public MapaIterator(ListaEnlazada<T> valores) {
            this.valores = valores;
            this.indiceActual = 0;
        }

        @Override
        public boolean hasNext() {
            return indiceActual < valores.getTamanio();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No hay más elementos en el mapa.");
            }
            T valorActual = valores.get(indiceActual);
            indiceActual++;
            return valorActual;
        }
    }
}