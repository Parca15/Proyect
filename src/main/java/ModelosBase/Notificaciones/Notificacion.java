package ModelosBase.Notificaciones;

import java.time.LocalDateTime;

public class Notificacion {
    private String titulo;
    private String mensaje;
    private TipoNotificacion tipo;
    private PrioridadNotificacion prioridad;
    private LocalDateTime fechaCreacion;
    private boolean leida;
    private String idReferencia;

    public Notificacion(String titulo, String mensaje, TipoNotificacion tipo,
                        PrioridadNotificacion prioridad, String idReferencia) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.fechaCreacion = LocalDateTime.now();
        this.leida = false;
        this.idReferencia = idReferencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public PrioridadNotificacion getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadNotificacion prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public String getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(String idReferencia) {
        this.idReferencia = idReferencia;
    }
}