package ModelosBase;

import jakarta.mail.MessagingException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BetterEmailSender {
    private final ExecutorService executorService;
    private final EmailSender emailSender = EmailSender.getInstance();
    private static BetterEmailSender instancia;

    public BetterEmailSender() {
        this.executorService = Executors.newFixedThreadPool(2);
    }

    public static BetterEmailSender getInstance() {
        if (instancia == null) {
            instancia = new BetterEmailSender();
        }
        return instancia;
    }

    public void enviarMailAsync(String titulo, String mensaje) {
        executorService.submit(() -> {
            try {
                String correo = "";
                FileReader fr = new FileReader("src/main/resources/Login_Archivo/UsuarioActual");
                BufferedReader br = new BufferedReader(fr);
                correo = br.readLine();
                emailSender.enviarEmail(correo, titulo, mensaje);
                br.close();
                fr.close();
            } catch (IOException | MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}