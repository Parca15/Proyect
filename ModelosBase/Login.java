package Proyecto.ModelosBase;

import Proyecto.ModelosBase.Inter.InterLogin;

import java.io.*;

public class Login {

    public static void main(String[] args) {
        // Inicia la interfaz de inicio de sesión
        java.awt.EventQueue.invokeLater(() -> new InterLogin().setVisible(true));
    }

    public static String verifyUser(String username, String password) {
        if (isUserInFile(username, password, "ModelosBase/Login_Archivo/Admin.txt")) {
            return "admin";
        } else if (isUserInFile(username, password, "ModelosBase/Login_Archivo/Usuarios.txt")) {
            return "user";
        }
        return null;
    }

    private static boolean isUserInFile(String username, String password, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("@@");

                if (userData.length >= 4) {
                    String storedUsername = userData[2];
                    String storedPassword = userData[3];

                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
