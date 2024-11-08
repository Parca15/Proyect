package ModelosBase;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import App.Login; // Asegúrate de que esta sea la ruta correcta donde está tu clase Login
import java.io.*;
import java.util.Properties;

public class RecuperarPassword {

    public static boolean enviarPasswordPorCorreo(String correo, String documento) {
        String passwordEncontrado = buscarPassword(correo, documento);

        if (passwordEncontrado != null) {
            return enviarCorreo(correo, passwordEncontrado);
        }
        return false;
    }

    private static String buscarPassword(String correo, String documento) {
        String[] archivos = {"Login_Archivo/Admin", "Login_Archivo/Usuarios"};

        for (String archivo : archivos) {
            try (InputStream inputStream = Login.class.getClassLoader().getResourceAsStream(archivo)) {
                // Verificar si el archivo fue encontrado
                if (inputStream == null) {
                    System.out.println("El archivo no se encontró: " + archivo);
                    continue; // Pasar al siguiente archivo
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] credentials = line.split("@@");
                        // Asumiendo que el documento está en credentials[2] y el correo en credentials[4]
                        if (credentials.length > 4 &&
                                credentials[2].trim().equals(documento) &&
                                credentials[4].trim().equals(correo)) {
                            return credentials[3]; // Retorna la contraseña
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null; // Retorna null si no se encuentra la contraseña
    }

    private static boolean enviarCorreo(String destinatario, String passwordUsuario) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Añade esta línea
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Credenciales del remitente (debes cambiar esto)
        final String username = "danielhisaza16@gmail.com";
        final String passwordGmail = "ylkybslrxbjksrfs"; // Usar contraseña de aplicación de Google

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, passwordGmail);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Recuperación de contraseña");
            message.setText("Su contraseña es: " + passwordUsuario +
                    "\n\nPor seguridad, le recomendamos cambiar su contraseña antes de iniciar sesión.");

            Transport.send(message);
            System.out.println("Correo enviado exitosamente"); // Añade este log
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error específico: " + e.getMessage()); // Añade este log
            return false;
        }
    }
}