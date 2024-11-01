package ModelosBase;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class WhatsAppSender {
    private final String phoneNumberId = "471467826048910";
    private final String accessToken = "EAAG1gibF9ZA4BOZCAP3VdqWUZBEmoPVA97K2ycBpNHMRFIHabnRsZAqKfIhvDSnmyLfZBnK3DOCGTzZAQMqZCCnExUsRpyxJ9ZALVtpOAPBNOFMbyBu2yXMCcHeoZA7WEzgoFYSZBESqVFtE5oVXU6KzsRBIwuHl1Uvb3BVtN71Eo7zfuiq89oLisRWnBHQhB0xsZCqls9QBXaliVE73hx6Keh63ljevgZDZD";
    private final OkHttpClient client;
    private static WhatsAppSender instance;

    public WhatsAppSender() {
        this.client = new OkHttpClient();
    }

    public static WhatsAppSender getInstance() {
        if (instance == null) {
            instance = new WhatsAppSender();
        }
        return instance;
    }

    public void enviarMensaje(String recipientNumber, String headerText, String userName, String bodyMessage) {
        try {
            JSONObject message = createMessageObject(recipientNumber, headerText, userName, bodyMessage);
            sendRequest(message);
        } catch (Exception e) {
            System.err.println("Error al enviar el mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JSONObject createMessageObject(String destinatario, String titulo, String usuario, String mensaje) {
        JSONObject message = new JSONObject();
        message.put("messaging_product", "whatsapp");
        message.put("to", destinatario);
        message.put("type", "template");

        JSONObject template = new JSONObject();
        template.put("name", "notificaciones");
        template.put("language", new JSONObject().put("code", "es"));

        // Crear componentes
        JSONArray components = new JSONArray();

        // Header component
        JSONObject headerComponent = new JSONObject();
        headerComponent.put("type", "header");
        JSONArray headerParameters = new JSONArray();
        headerParameters.put(new JSONObject()
                .put("type", "text")
                .put("text", titulo));
        headerComponent.put("parameters", headerParameters);
        components.put(headerComponent);

        // Body component
        JSONObject bodyComponent = new JSONObject();
        bodyComponent.put("type", "body");
        JSONArray bodyParameters = new JSONArray();
        bodyParameters.put(new JSONObject()
                .put("type", "text")
                .put("text", usuario));
        bodyParameters.put(new JSONObject()
                .put("type", "text")
                .put("text", mensaje));
        bodyComponent.put("parameters", bodyParameters);
        components.put(bodyComponent);

        template.put("components", components);
        message.put("template", template);

        return message;
    }

    private void sendRequest(JSONObject message) throws Exception {
        Request request = new Request.Builder()
                .url("https://graph.facebook.com/v17.0/" + phoneNumberId + "/messages")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(
                        message.toString(),
                        MediaType.parse("application/json")
                ))
                .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;
        String responseBody = response.body().string();

        if (!response.isSuccessful()) {
            System.err.println("Error al enviar el mensaje. Código: " + response.code());
            System.err.println("Detalles: " + responseBody);
            return;
        }

        System.out.println("¡Mensaje enviado con éxito!");
        System.out.println("Respuesta: " + responseBody);
    }
}