package Funcionalidades;

import EstructurasDatos.Cola;
import ModelosBase.*;
import Notificaciones.Notificacion;
import Notificaciones.PrioridadNotificacion;
import Notificaciones.TipoNotificacion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class GestionNotificaciones {
    private static GestionNotificaciones instancia;
    private Cola<Notificacion> notificaciones;
    private Consumer<Notificacion> manejoNotificacion;
    private EmailSender emailSender = EmailSender.getInstance();
    private BetterEmailSender betterEmailSender = BetterEmailSender.getInstance();
    private WhatsAppSender whatsAppSender = WhatsAppSender.getInstance();

    private GestionNotificaciones() {
        this.notificaciones = new Cola<>();
    }

    public static GestionNotificaciones getInstance() {
        if (instancia == null) {
            instancia = new GestionNotificaciones();
        }
        return instancia;
    }

    public void setNotificationHandler(Consumer<Notificacion> handler) {
        this.manejoNotificacion = handler;
    }

    public void verificarTareasPendientes(Proceso proceso) {
        LocalDateTime ahora = LocalDateTime.now();

        for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
            Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
            Cola<Tarea> tareas = actividad.getTareas();

            Tarea tarea = tareas.obtenerFrente();
            while (tarea != null) {
                long minutosTranscurridos = ChronoUnit.MINUTES.between(tarea.getFechaCreacion(), ahora);

                if (tarea.isObligatoria() && minutosTranscurridos > tarea.getDuracion()) {
                    crearNotificacion(
                            "Tarea Vencida",
                            "La tarea '" + tarea.getDescripcion() + "' en la actividad '" + actividad.getNombre() + "' está vencida",
                            TipoNotificacion.TAREA_VENCIDA,
                            PrioridadNotificacion.ALTA,
                            proceso.getId().toString()
                    );
                } else if (tarea.isObligatoria() && minutosTranscurridos >= tarea.getDuracion() - 30) {
                    crearNotificacion(
                            "Tarea Próxima a Vencer",
                            "La tarea '" + tarea.getDescripcion()
                                    + "' en la actividad '" + actividad.getNombre()
                                    + "' vencerá en 30 minutos",
                            TipoNotificacion.TAREA_PROXIMA,
                            PrioridadNotificacion.MEDIA,
                            proceso.getId().toString()
                    );
                }
                tarea = tareas.desencolar();
            }
        }
    }

    public void notificarActividadCreada(Actividad actividad, Proceso proceso) {
        crearNotificacion(
                "Actividad Creada",
                "La actividad '" + actividad.getNombre() + "' ha sido creada en el proceso '" +
                        proceso.getNombre() + "'",
                TipoNotificacion.ACTIVIDAD_CREADA,
                actividad.isObligatoria() ? PrioridadNotificacion.ALTA : PrioridadNotificacion.MEDIA,
                proceso.getId().toString()
        );
    }

    public void notificarTareaCreada(Tarea tarea, Actividad actividad, Proceso proceso) {
        crearNotificacion(
                "Nueva Tarea Creada",
                "Se ha creado la tarea '" + tarea.getDescripcion() + "' en la actividad '" +
                        actividad.getNombre() + "' del proceso '" + proceso.getNombre() + "'",
                TipoNotificacion.TAREA_CREADA,
                tarea.isObligatoria() ? PrioridadNotificacion.ALTA : PrioridadNotificacion.MEDIA,
                proceso.getId().toString()
        );
    }

    public void crearNotificacion(String titulo, String mensaje, TipoNotificacion tipo, PrioridadNotificacion prioridad, String idReferencia) {
        Notificacion notificacion = new Notificacion(titulo, mensaje, tipo, prioridad, idReferencia);
        notificaciones.encolar(notificacion);
        if (manejoNotificacion != null) {
            manejoNotificacion.accept(notificacion);
            enviarNotificaciones(titulo, mensaje);
        }
    }

    private void enviarNotificaciones(String titulo, String mensaje) {
        betterEmailSender.enviarMailAsync(titulo, mensaje);

        new Thread(() -> {
            try {
                String telefono = "";
                String nombre = "";
                try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/Login_Archivo/UsuarioActual"))) {
                    br.readLine();
                    telefono = br.readLine();
                    nombre = br.readLine();
                }

                if (telefono != null && !telefono.isEmpty()) {
                    if (!telefono.startsWith("57")) {
                        telefono = "57" + telefono;
                    }

                    whatsAppSender.enviarMensaje(
                            telefono,
                            titulo,  // Texto del header
                            nombre, // Nombre del usuario
                            mensaje  // Mensaje del body
                    );
                }
            } catch (IOException e) {
                System.err.println("Error al leer el número de teléfono: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Error al enviar mensaje de WhatsApp: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public Cola<Notificacion> obtenerNotificaciones() {
        return notificaciones;
    }

    public void limpiarNotificaciones() {
        while (!notificaciones.estaVacia()) {
            notificaciones.desencolar();
        }
    }

    public void notificarProcesoIniciado(Proceso proceso) {
        crearNotificacion(
                "Nuevo Proceso",
                "Se ha creado el proceso: " + proceso.getNombre(),
                TipoNotificacion.PROCESO_CREADO,
                PrioridadNotificacion.BAJA,
                proceso.getId().toString()
        );
    }

    public void notificarTareaVencida(Tarea tarea, Actividad actividad, Proceso proceso) {
        crearNotificacion(
                "Tarea Vencida",
                "La tarea '" + tarea.getDescripcion() + "' en la actividad '" +
                        actividad.getNombre() + "' ha superado su duración estimada de " +
                        tarea.getDuracion() + " minutos",
                TipoNotificacion.TAREA_VENCIDA,
                PrioridadNotificacion.ALTA,
                proceso.getId().toString()
        );
    }

    public void notificarTareaProximaVencer(Tarea tarea, Actividad actividad, Proceso proceso, long minutosRestantes) {
        crearNotificacion(
                "Tarea Próxima a Vencer",
                "La tarea '" + tarea.getDescripcion() + "' vencerá en " +
                        minutosRestantes + " minutos" + " en la actividad '" + actividad.getNombre() + "'",
                TipoNotificacion.TAREA_PROXIMA,
                PrioridadNotificacion.MEDIA,
                proceso.getId().toString()
        );
    }

    public void notificarActividadEnRiesgo(Actividad actividad, Proceso proceso) {
        crearNotificacion(
                "Actividad en Riesgo",
                "La actividad '" + actividad.getNombre() + "' tiene tareas vencidas",
                TipoNotificacion.ACTIVIDAD_EN_RIESGO,
                PrioridadNotificacion.ALTA,
                proceso.getId().toString()
        );
    }
}