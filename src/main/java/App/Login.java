package App;

import Interfaz.InterLogin;
import java.io.*;

public class Login {
    public static String verifyUser(String username, String password) {
        // Usar la ruta correcta que coincide con la estructura del proyecto
        if (isUserInFile(username, password, "/Login_Archivo/Admin")) {
            return "admin";
        } else if (isUserInFile(username, password, "/Login_Archivo/Usuarios")) {
            return "user";
        }
        return null;
    }

    private static boolean isUserInFile(String username, String password, String resourcePath) {
        // Obtener el recurso usando la ruta absoluta desde el classpath
        InputStream inputStream = Login.class.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            System.err.println("No se pudo encontrar el archivo: " + resourcePath);
            // Para debug
            System.err.println("Intentando cargar desde: " + resourcePath);
            System.err.println("ClassLoader base path: " + Login.class.getResource("/"));
            return false;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split("@@");
                if (credentials.length >= 4 &&
                        credentials[2].trim().equals(username) &&
                        credentials[3].trim().equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String extraerCorreo(String usuario, String contraseña, String resourcePath) {
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/Login_Archivo/" + resourcePath;
        }

        InputStream inputStream = Login.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            System.err.println("No se pudo encontrar el archivo: " + resourcePath);
            return "";
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split("@@");
                if (credentials.length >= 5 &&
                        credentials[2].trim().equals(usuario) &&
                        credentials[3].trim().equals(contraseña)) {
                    return credentials[4];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String extraerTelefono(String usuario, String contraseña, String resourcePath) {
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/Login_Archivo/" + resourcePath;
        }

        InputStream inputStream = Login.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            System.err.println("No se pudo encontrar el archivo: " + resourcePath);
            return "";
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split("@@");
                if (credentials.length >= 6 &&
                        credentials[2].trim().equals(usuario) &&
                        credentials[3].trim().equals(contraseña)) {
                    return credentials[5];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String extraerNombre(String usuario, String contraseña, String resourcePath) {
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/Login_Archivo/" + resourcePath;
        }

        InputStream inputStream = Login.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            System.err.println("No se pudo encontrar el archivo: " + resourcePath);
            return "";
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split("@@");
                if (credentials.length >= 4 &&
                        credentials[2].trim().equals(usuario) &&
                        credentials[3].trim().equals(contraseña)) {
                    return credentials[0] + " " + credentials[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new InterLogin().setVisible(true));
    }
}



