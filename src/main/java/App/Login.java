package App;

import Interfaz.InterLogin;
import java.io.*;

public class Login {

    public static void main(String[] args) {
        // Inicia la interfaz de inicio de sesión
        java.awt.EventQueue.invokeLater(() -> new InterLogin().setVisible(true));
    }

    public static String verifyUser(String username, String password) {
        System.out.println("Verificando usuario: " + username);

        if (isUserInFile(username, password, "Login_Archivo/Admin")) {
            System.out.println("Usuario verificado como administrador.");
            return "admin";
        } else if (isUserInFile(username, password, "Login_Archivo/Usuarios")) {
            System.out.println("Usuario verificado como usuario regular.");
            return "user";
        }

        System.out.println("Usuario no encontrado.");
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
}
