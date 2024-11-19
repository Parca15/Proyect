package App;

import EstructurasDatos.ListaEnlazada;
import EstructurasDatos.Nodo;
import Interfaz.InterLogin;
import java.io.*;

public class Login {
    public static String tipoUsuario;

    public static String verifyUser(String username, String password) {
        if (isUserInFile(username, password, "Login_Archivo/Admin")) {
            tipoUsuario = "admin";
            return "admin";
        } else if (isUserInFile(username, password, "Login_Archivo/Usuarios")) {
            tipoUsuario = "user";
            return "user";
        }
        return null;
    }

    private static boolean isUserInFile(String username, String password, String resourcePath) {
        try {
            // Convertir ruta de recurso a ruta de archivo del sistema
            File file = new File("src/main/resources/" + resourcePath);
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] credentials = line.split("@@");
                    if (credentials[2].trim().equals(username) && credentials[3].trim().equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateUserPassword(String username, String oldPassword, String newPassword, String userType) {
        String resourcePath = userType.equals("admin") ?
                "src/main/resources/Login_Archivo/Admin" :
                "src/main/resources/Login_Archivo/Usuarios";

        File inputFile = new File(resourcePath);

        try {
            // Cargar las líneas del archivo en una ListaEnlazada
            ListaEnlazada<String> lineas = new ListaEnlazada<>();
            boolean updated = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] credentials = line.split("@@");
                    if (credentials[2].trim().equals(username) && credentials[3].trim().equals(oldPassword)) {
                        // Actualizar la contraseña
                        credentials[3] = newPassword.trim();
                        line = String.join("@@", credentials);
                        updated = true;
                    }
                    lineas.insertar(line);
                }
            }

            // Reescribir el archivo con los cambios
            if (updated) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile))) {
                    Nodo<String> actual = lineas.getCabeza();
                    while (actual != null) {
                        writer.write(actual.getValorNodo() + System.lineSeparator());
                        actual = actual.getSiguienteNodo();
                    }
                }
            }

            return updated;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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

    public static String extraerTelefono(String usuario, String contraseña, String resourcePath) {
        String telefono = "";
        if (isUserInFile(usuario, contraseña, resourcePath)) {
            try (InputStream inputStream = Login.class.getClassLoader().getResourceAsStream(resourcePath);
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] credentials = line.split("@@");
                    if (credentials[2].trim().equals(usuario) && credentials[3].trim().equals(contraseña)) {
                        telefono = credentials[5];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return telefono;
    }

    public static String extraerNombre(String usuario, String contraseña, String resourcePath) {
        String nombre = "";
        if (isUserInFile(usuario, contraseña, resourcePath)) {
            try (InputStream inputStream = Login.class.getClassLoader().getResourceAsStream(resourcePath);
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] credentials = line.split("@@");
                    if (credentials[2].trim().equals(usuario) && credentials[3].trim().equals(contraseña)) {
                        nombre = credentials[0] +" "+ credentials[1];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return nombre;
    }
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new InterLogin().setVisible(true));
    }
}