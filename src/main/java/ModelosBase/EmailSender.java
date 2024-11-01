package ModelosBase;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.AddressException;
import java.util.Properties;
import java.util.regex.Pattern;

public class EmailSender {
    private final String username = "danielhisaza16@gmail.com";
    private final String password = "ylkybslrxbjksrfs";
    private final Properties prop;
    private static EmailSender instance;

    // Patrón simple para validar formato de email
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public EmailSender() {
        prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
    }

    public static EmailSender getInstance() {
        if (instance == null) {
            instance = new EmailSender();
        }
        return instance;
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public void enviarEmail(String destinatario, String asunto, String mensaje) throws MessagingException {
        // Validar el email del remitente
        if (!isValidEmail(username)) {
            throw new MessagingException("Dirección de correo del remitente inválida: " + username);
        }

        // Validar el email del destinatario
        if (!isValidEmail(destinatario)) {
            throw new MessagingException("Dirección de correo del destinatario inválida: " + destinatario);
        }

        try {
            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);

            // Configurar remitente
            try {
                message.setFrom(new InternetAddress(username, true));
            } catch (AddressException e) {
                throw new MessagingException("Error al configurar el remitente: " + e.getMessage());
            }

            // Configurar destinatario
            try {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario, true));
            } catch (AddressException e) {
                throw new MessagingException("Error al configurar el destinatario: " + e.getMessage());
            }

            message.setSubject(asunto);
            message.setText(mensaje);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new MessagingException("Error al enviar el correo: " + e.getMessage());
        }
    }
}