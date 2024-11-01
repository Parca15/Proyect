package ModelosBase;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class WhatsAppSender {
    private final String phoneNumberId = "471467826048910";
    private final String accessToken = "EAAG1gibF9ZA4BOwpGRWlI6qvwahMrBn7EVXPEgl3rzAOGdZBtbmjk2g2Ge3WGbaPgTJ0jDJFUz3ghoQT06XHuZAhK7n4IhwClsbvGwmhqmjqWr0WqWn6kisYrrzeUML890wDsCZB5CKnylPDE4CGIDTZC6j8vosmkD8y3FxVjJnF9Na0Aa1EkgxsExZBICN9ZCkG32MkZCOZAiDQZBhn01zlmjAuZAYhwZDZD";
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

    public void enviarMensaje(String destinatario, String titulo, String usuario, String mensaje) {
        try {
            JSONObject message = createMessageObject(destinatario, titulo, usuario, mensaje);
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
        }
    }
}