package Proyecto.EstructurasDatos;


public class Cola<T> {
    private Nodo<T> nodoPrimero;
    private Nodo<T> nodoUltimo;
    private int tamanio;

    public Cola() {
        nodoPrimero = null;
        nodoUltimo = null;
        tamanio = 0;
    }

    public boolean estaVacia() {
        return nodoPrimero == null;
    }

    public void encolar(T elemento) {
        Nodo<T> nodoAux = new Nodo<>(elemento);
        if (estaVacia()) {
            nodoPrimero = nodoUltimo = nodoAux;
        } else {
            nodoUltimo.setSiguienteNodo(nodoAux);
            nodoUltimo = nodoAux;
        }
        tamanio++;
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;  // Opcionalmente, puedes lanzar una excepción si prefieres manejar el caso de cola vacía.
        }
        T valor = nodoPrimero.getValorNodo();
        nodoPrimero = nodoPrimero.getSiguienteNodo();
        tamanio--;

        if (nodoPrimero == null) { // Si la cola queda vacía después de desencolar
            nodoUltimo = null;
        }

        return valor;
    }

    public T obtenerFrente() {
        if (estaVacia()) {
            return null;  // Opcionalmente, lanzar excepción para cola vacía
        }
        return nodoPrimero.getValorNodo();
    }
    public void insertarDespuesDe(T elementoExistente, T nuevoElemento) {


        if (estaVacia()) {
            encolar(nuevoElemento);
            return;
        }

        Nodo<T> actual = nodoPrimero;
        boolean encontrado = false;

        while (actual != null && !encontrado) {
            if (actual.getValorNodo().equals(elementoExistente)) {
                Nodo<T> nuevoNodo = new Nodo<>(nuevoElemento);
                nuevoNodo.setSiguienteNodo(actual.getSiguienteNodo());
                actual.setSiguienteNodo(nuevoNodo);

                // Si el elemento existente era el último, actualizar nodoUltimo
                if (actual == nodoUltimo) {
                    nodoUltimo = nuevoNodo;
                }

                tamanio++;
                encontrado = true;
            }
            actual = actual.getSiguienteNodo();
        }

        if (!encontrado) {
            System.out.println("El elemento existente no se encuentra en la cola.");
        }
    }

    public int obtenerTamanio() {
        return tamanio;
    }

    // Métodos getter y setter para atributos privados
    public Nodo<T> getNodoPrimero() {
        return nodoPrimero;
    }

    public void setNodoPrimero(Nodo<T> nodoPrimero) {
        this.nodoPrimero = nodoPrimero;
    }

    public Nodo<T> getNodoUltimo() {
        return nodoUltimo;
    }

    public void setNodoUltimo(Nodo<T> nodoUltimo) {
        this.nodoUltimo = nodoUltimo;
    }

    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }

    public Nodo<T> obtenerNodoPosicion(int posicion) {
        if (posicion < 0 || posicion >= tamanio) {
            System.out.println("Posición fuera de rango.");
            return null;
        }

        Nodo<T> actual = nodoPrimero;
        int contador = 0;

        while (actual != null && contador < posicion) {
            actual = actual.getSiguienteNodo();
            contador++;
        }

        return actual;
    }

}
