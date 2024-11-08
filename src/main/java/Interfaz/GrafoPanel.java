package Interfaz;

import ModelosBase.Actividad;
import ModelosBase.Proceso;
import ModelosBase.Tarea;
import Funcionalidades.GestionProcesos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class GrafoPanel extends JPanel {
    private final GestionProcesos gestionProcesos;
    private JFXPanel jfxPanel;
    private WebView webView;
    private JComboBox<String> tipoVistaCombo;
    private static final String HTML_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/mermaid/9.1.7/mermaid.min.js"></script>
            <script>
                mermaid.initialize({
                    startOnLoad: true,
                    theme: 'default',
                    securityLevel: 'loose'
                });
            </script>
        </head>
        <body>
            <div class="mermaid">
                %s
            </div>
        </body>
        </html>
    """;

    public GrafoPanel(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
        setLayout(new BorderLayout(10, 10));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Panel superior para controles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Selector de tipo de vista
        tipoVistaCombo = new JComboBox<>(new String[]{
                "Vista Completa",
                "Solo Procesos y Actividades",
                "Solo Procesos"
        });

        JButton generarGrafoButton = new JButton("Generar Grafo");
        generarGrafoButton.addActionListener(e -> generarGrafo());

        controlPanel.add(new JLabel("Tipo de Vista:"));
        controlPanel.add(tipoVistaCombo);
        controlPanel.add(generarGrafoButton);

        // Inicializar el panel JavaFX
        jfxPanel = new JFXPanel();
        Platform.runLater(this::inicializarWebView);

        // Agregar componentes al panel principal
        add(controlPanel, BorderLayout.NORTH);
        add(jfxPanel, BorderLayout.CENTER);
    }

    private void inicializarWebView() {
        webView = new WebView();
        Scene scene = new Scene(webView);
        jfxPanel.setScene(scene);
    }

    private void generarGrafo() {
        StringBuilder mermaidCode = new StringBuilder("graph TB\n");
        Map<UUID, Proceso> procesos = gestionProcesos.getProcesos();
        String tipoVista = (String) tipoVistaCombo.getSelectedItem();

        int contadorActividades = 0;
        int contadorTareas = 0;

        // Agregar nodo empresa
        mermaidCode.append("empresa((\"Empresa\"))\n");

        // Primero crear todos los nodos
        for (Proceso proceso : procesos.values()) {
            String procesoId = "proc_" + proceso.getId().toString().substring(0, 8);
            mermaidCode.append(procesoId)
                    .append("((\"")
                    .append(proceso.getNombre())
                    .append("\"))\n");

            // Conectar con empresa
            mermaidCode.append("empresa").append(" --- ").append(procesoId).append("\n");

            if ("Solo Procesos".equals(tipoVista)) {
                continue;
            }

            for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                String actividadId = "act_" + contadorActividades++;

                mermaidCode.append(actividadId)
                        .append("((\"")
                        .append(actividad.getNombre())
                        .append(actividad.isObligatoria() ? " (Obl)" : " (Opt)")
                        .append("\"))\n");

                // Conectar con el proceso correspondiente
                mermaidCode.append(procesoId)
                        .append(" --- ")
                        .append(actividadId)
                        .append("\n");

                if (!"Solo Procesos y Actividades".equals(tipoVista)) {
                    var nodoTarea = actividad.getTareas().getNodoPrimero();
                    while (nodoTarea != null) {
                        Tarea tarea = nodoTarea.getValorNodo();
                        String tareaId = "tar_" + contadorTareas++;

                        mermaidCode.append(tareaId)
                                .append("((\"")
                                .append(tarea.getDescripcion())
                                .append(tarea.isObligatoria() ? " (Obl)" : " (Opt)")
                                .append(tarea.isFinalizada() ? " ✓" : "")
                                .append("\"))\n");

                        // Conectar con la actividad correspondiente
                        mermaidCode.append(actividadId)
                                .append(" --- ")
                                .append(tareaId)
                                .append("\n");

                        nodoTarea = nodoTarea.getSiguienteNodo();
                    }
                }
            }
        }

        String htmlContent = String.format(HTML_TEMPLATE, mermaidCode.toString());
        Platform.runLater(() -> webView.getEngine().loadContent(htmlContent));
    }
}