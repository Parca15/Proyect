package EstructurasDatos;

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
            return null;
        }
        T valor = nodoPrimero.getValorNodo();
        nodoPrimero = nodoPrimero.getSiguienteNodo();
        tamanio--;

        if (nodoPrimero == null) {
            nodoUltimo = null;
        }

        return valor;
    }

    public T obtenerFrente() {
        if (estaVacia()) {
            return null;
        }
        return nodoPrimero.getValorNodo();
    }

    public void insertarAlInicio(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (estaVacia()) {
            nodoPrimero = nodoUltimo = nuevoNodo;
        } else {
            nuevoNodo.setSiguienteNodo(nodoPrimero);
            nodoPrimero = nuevoNodo;
        }
        tamanio++;
    }

    public void insertarAlFinal(T elemento) {
        encolar(elemento);
    }

    public void insertarEnPosicion(int posicion, T elemento) {
        if (posicion < 0 || posicion > tamanio) {
            throw new IllegalArgumentException("Posición inválida");
        }

        if (posicion == 0) {
            insertarAlInicio(elemento);
            return;
        }

        if (posicion == tamanio) {
            insertarAlFinal(elemento);
            return;
        }

        Nodo<T> actual = nodoPrimero;
        for (int i = 0; i < posicion - 1; i++) {
            actual = actual.getSiguienteNodo();
        }

        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        nuevoNodo.setSiguienteNodo(actual.getSiguienteNodo());
        actual.setSiguienteNodo(nuevoNodo);
        tamanio++;
    }

    public void insertarDespuesDe(T elementoExistente, T nuevoElemento) {
        if (estaVacia()) {
            encolar(nuevoElemento);
            return;
        }

        Nodo<T> actual = nodoPrimero;
        while (actual != null) {
            if (actual.getValorNodo().equals(elementoExistente)) {
                Nodo<T> nuevoNodo = new Nodo<>(nuevoElemento);
                nuevoNodo.setSiguienteNodo(actual.getSiguienteNodo());
                actual.setSiguienteNodo(nuevoNodo);

                if (actual == nodoUltimo) {
                    nodoUltimo = nuevoNodo;
                }
                tamanio++;
                return;
            }
            actual = actual.getSiguienteNodo();
        }
        throw new IllegalArgumentException("Elemento existente no encontrado en la cola");
    }

    public int obtenerTamanio() {
        return tamanio;
    }

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
            throw new IllegalArgumentException("Posición fuera de rango");
        }

        Nodo<T> actual = nodoPrimero;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguienteNodo();
        }

        return actual;
    }
}