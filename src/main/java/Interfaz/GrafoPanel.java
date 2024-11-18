package Interfaz;

import EstructurasDatos.Mapa;
import ModelosBase.Actividad;
import ModelosBase.Proceso;
import ModelosBase.Tarea;
import Funcionalidades.GestionProcesos;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
import java.util.*;
import java.util.List;

public class GrafoPanel extends JPanel {
    private final GestionProcesos gestionProcesos;
    private JComboBox<String> tipoVistaCombo;
    private Map<String, Point> nodePosiciones;
    private List<ConnectionLine> conexiones;
    private Map<String, String> nodoEtiquetas;
    private Map<String, Color> nodoColores;
    private static final int NODE_RADIUS = 35;
    private static final int VERTICAL_SPACING = 120;
    private static final int HORIZONTAL_SPACING = 200;
    private JPanel drawingPanel;

    private static class ConnectionLine {
        Point start;
        Point end;
        boolean isCurved;

        ConnectionLine(Point start, Point end, boolean isCurved) {
            this.start = start;
            this.end = end;
            this.isCurved = isCurved;
        }
    }

    public GrafoPanel(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
        this.nodePosiciones = new HashMap<>();
        this.conexiones = new ArrayList<>();
        this.nodoEtiquetas = new HashMap<>();
        this.nodoColores = new HashMap<>();
        setLayout(new BorderLayout(10, 10));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Panel superior para controles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(245, 245, 250));

        // Selector de tipo de vista con estilo mejorado
        tipoVistaCombo = new JComboBox<>(new String[]{
                "Vista Completa",
                "Solo Procesos y Actividades",
                "Solo Procesos"
        });
        tipoVistaCombo.setPreferredSize(new Dimension(200, 30));

        JButton generarGrafoButton = new JButton("Generar Grafo");
        generarGrafoButton.setPreferredSize(new Dimension(120, 30));
        generarGrafoButton.setBackground(new Color(138, 43, 226)); // Violeta más brillante
        generarGrafoButton.setForeground(Color.WHITE);
        generarGrafoButton.setFocusPainted(false);
        generarGrafoButton.setBorderPainted(false);
        generarGrafoButton.setOpaque(true);

        controlPanel.add(new JLabel("Tipo de Vista:"));
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(tipoVistaCombo);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(generarGrafoButton);

        // Panel para dibujar el grafo
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGrafo((Graphics2D) g);
            }
        };
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setPreferredSize(new Dimension(1000, 800));
        generarGrafoButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                generarGrafoButton.setBackground(new Color(148, 53, 236)); // Violeta más claro para hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                generarGrafoButton.setBackground(new Color(138, 43, 226)); // Violeta original
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                generarGrafoButton.setBackground(new Color(128, 33, 216)); // Violeta más oscuro para click
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                generarGrafoButton.setBackground(new Color(138, 43, 226)); // Volver al violeta original
            }
        });
        generarGrafoButton.addActionListener(e -> {
            generarGrafo();
            drawingPanel.repaint();
        });
        // Agregar componentes al panel principal
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
    }

    private void generarGrafo() {
        nodePosiciones.clear();
        conexiones.clear();
        nodoEtiquetas.clear();
        nodoColores.clear();

        Mapa<UUID, Proceso> procesos = gestionProcesos.getProcesos();
        String tipoVista = (String) tipoVistaCombo.getSelectedItem();

        // Posicionar nodo empresa
        Point empresaPos = new Point(500, 50);
        nodePosiciones.put("empresa", empresaPos);
        nodoEtiquetas.put("empresa", "Empresa");
        nodoColores.put("empresa", new Color(103, 58, 183));

        int numProcesos = procesos.size();
        int startX = 500 - (numProcesos * HORIZONTAL_SPACING) / 2;
        int currentY = 200;

        int procesoIndex = 0;
        for (Proceso proceso : procesos) {
            String procesoId = "proc_" + proceso.getId().toString().substring(0, 8);
            Point procesoPos = new Point(startX + procesoIndex * HORIZONTAL_SPACING, currentY);
            nodePosiciones.put(procesoId, procesoPos);
            nodoEtiquetas.put(procesoId, proceso.getNombre());
            nodoColores.put(procesoId, new Color(144, 202, 249));
            conexiones.add(new ConnectionLine(empresaPos, procesoPos, true));

            if ("Solo Procesos".equals(tipoVista)) {
                procesoIndex++;
                continue;
            }

            // Posicionar actividades
            int numActividades = proceso.getActividades().getTamanio();
            int actStartX = procesoPos.x - ((numActividades - 1) * HORIZONTAL_SPACING / 2);

            for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                String actividadId = "act_" + procesoId + "_" + i;
                Point actividadPos = new Point(actStartX + i * HORIZONTAL_SPACING, currentY + VERTICAL_SPACING);

                nodePosiciones.put(actividadId, actividadPos);
                nodoEtiquetas.put(actividadId, actividad.getNombre() +
                        (actividad.isObligatoria() ? " ★" : " ☆"));
                nodoColores.put(actividadId, new Color(179, 157, 219));
                conexiones.add(new ConnectionLine(procesoPos, actividadPos, false));

                if (!"Solo Procesos y Actividades".equals(tipoVista)) {
                    // Posicionar tareas
                    var nodoTarea = actividad.getTareas().getNodoPrimero();
                    int tareaIndex = 0;
                    while (nodoTarea != null) {
                        Tarea tarea = nodoTarea.getValorNodo();
                        String tareaId = "tar_" + actividadId + "_" + tareaIndex;
                        Point tareaPos = new Point(actividadPos.x,
                                actividadPos.y + VERTICAL_SPACING + tareaIndex * 70);

                        nodePosiciones.put(tareaId, tareaPos);
                        nodoEtiquetas.put(tareaId, tarea.getDescripcion() +
                                (tarea.isObligatoria() ? " ★" : " ☆") +
                                (tarea.isFinalizada() ? " ✓" : ""));
                        nodoColores.put(tareaId, new Color(209, 196, 233));
                        conexiones.add(new ConnectionLine(actividadPos, tareaPos, false));

                        nodoTarea = nodoTarea.getSiguienteNodo();
                        tareaIndex++;
                    }
                }
            }
            procesoIndex++;
        }

        actualizarTamanoPanel();
    }

    private void actualizarTamanoPanel() {
        int maxX = 0, maxY = 0;
        for (Point p : nodePosiciones.values()) {
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }
        drawingPanel.setPreferredSize(new Dimension(maxX + 400, maxY + 300));
        drawingPanel.revalidate();
    }

    private void dibujarGrafo(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dibujar conexiones
        g2d.setStroke(new BasicStroke(2f));
        for (ConnectionLine conexion : conexiones) {
            if (conexion.isCurved) {
                // Crear una curva cuadrática para conexiones curvas
                int controlX = (conexion.start.x + conexion.end.x) / 2;
                int controlY = (conexion.start.y + conexion.end.y) / 2 - 30;

                QuadCurve2D curve = new QuadCurve2D.Float(
                        conexion.start.x, conexion.start.y,
                        controlX, controlY,
                        conexion.end.x, conexion.end.y
                );

                g2d.setColor(new Color(158, 158, 158, 180));
                g2d.draw(curve);
            } else {
                // Líneas rectas para el resto de conexiones
                g2d.setColor(new Color(158, 158, 158, 150));
                g2d.drawLine(conexion.start.x, conexion.start.y,
                        conexion.end.x, conexion.end.y);
            }
        }

        // Dibujar nodos
        for (Map.Entry<String, Point> entry : nodePosiciones.entrySet()) {
            Point pos = entry.getValue();
            String id = entry.getKey();
            String etiqueta = nodoEtiquetas.get(id);
            Color color = nodoColores.get(id);

            // Dibujar sombra
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fill(new Ellipse2D.Double(pos.x - NODE_RADIUS + 3, pos.y - NODE_RADIUS + 3,
                    NODE_RADIUS * 2, NODE_RADIUS * 2));

            // Dibujar círculo
            g2d.setColor(color);
            g2d.fill(new Ellipse2D.Double(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                    NODE_RADIUS * 2, NODE_RADIUS * 2));
            g2d.setColor(new Color(103, 58, 183, 100));
            g2d.setStroke(new BasicStroke(2f));
            g2d.draw(new Ellipse2D.Double(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                    NODE_RADIUS * 2, NODE_RADIUS * 2));

            // Dibujar etiqueta con fuente mejorada
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            FontMetrics fm = g2d.getFontMetrics();
            String[] lines = etiqueta.split(" ");
            int lineHeight = fm.getHeight();
            int y = pos.y - ((lines.length - 1) * lineHeight) / 2;

            for (String line : lines) {
                int textWidth = fm.stringWidth(line);
                g2d.setColor(Color.BLACK);
                g2d.drawString(line, pos.x - textWidth / 2, y + fm.getAscent());
                y += lineHeight;
            }
        }
    }
}