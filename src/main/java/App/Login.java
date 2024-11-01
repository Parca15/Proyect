package App;

import Interfaz.InterLogin;
import java.io.*;

public class Login {

    public static void main(String[] args) {
        // Inicia la interfaz de inicio de sesión
        java.awt.EventQueue.invokeLater(() -> new InterLogin().setVisible(true));
    }

    public static String verifyUser(String username, String password) {
        if (isUserInFile(username, password, "Login_Archivo/Admin")) {
            return "admin";
        } else if (isUserInFile(username, password, "Login_Archivo/Usuarios")) {
            return "user";
        }
        return null;
    }

    private static boolean isUserInFile(String username, String password, String resourcePath) {
        try (InputStream inputStream = Login.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split("@@");
                if (credentials[2].trim().equals(username) && credentials[3].trim().equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String extraerCorreo(String usuario, String contraseña, String resourcePath) {
        String correo = "";
        if (isUserInFile(usuario, contraseña, resourcePath)) {
            try (InputStream inputStream = Login.class.getClassLoader().getResourceAsStream(resourcePath);
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] credentials = line.split("@@");
                    if (credentials[2].trim().equals(usuario) && credentials[3].trim().equals(contraseña)) {
                        correo = credentials[4];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return correo;
    }
}
