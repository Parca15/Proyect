package Interfaz;

import Funcionalidades.GestionProcesos;
import ModelosBase.Actividad;
import ModelosBase.Proceso;
import ModelosBase.Tarea;
import EstructurasDatos.Nodo;
import EstructurasDatos.ListaEnlazada;
import EstructurasDatos.Cola;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.UUID;

public class DashboardPanel extends JPanel {
    private final GestionProcesos gestionProcesos;
    private JPanel statsPanel;
    private JPanel chartsPanel;

    private FlipPanel totalProcesosFlipPanel;
    private FlipPanel totalActividadesFlipPanel;
    private FlipPanel totalTareasFlipPanel;
    private FlipPanel tareasObligatoriasFlipPanel;

    private EstadisticaPanel totalProcesosPanel;
    private EstadisticaPanel totalActividadesPanel;
    private EstadisticaPanel totalTareasPanel;
    private EstadisticaPanel tareasObligatoriasPanel;

    private DetalleEstadisticaPanel detalleProcesosPanel;
    private DetalleEstadisticaPanel detalleActividadesPanel;
    private DetalleEstadisticaPanel detalleTareasPanel;
    private DetalleEstadisticaPanel detalleTareasObligatoriasPanel;

    private JButton btnActualizar;

    public DashboardPanel(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
        setupLayout();

        // Primera actualización inmediata
        actualizarEstadisticas();
    }

    private void initializeComponents() {
        // Panel de estadísticas
        statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBackground(new Color(111, 63, 182));

        // Crear paneles de estadísticas y detalles
        totalProcesosPanel = new EstadisticaPanel("Total Procesos");
        totalActividadesPanel = new EstadisticaPanel("Total Actividades");
        totalTareasPanel = new EstadisticaPanel("Total Tareas");
        tareasObligatoriasPanel = new EstadisticaPanel("Tareas Obligatorias");

        detalleProcesosPanel = new DetalleEstadisticaPanel();
        detalleActividadesPanel = new DetalleEstadisticaPanel();
        detalleTareasPanel = new DetalleEstadisticaPanel();
        detalleTareasObligatoriasPanel = new DetalleEstadisticaPanel();

        // Crear FlipPanels
        totalProcesosFlipPanel = new FlipPanel(totalProcesosPanel, detalleProcesosPanel);
        totalActividadesFlipPanel = new FlipPanel(totalActividadesPanel, detalleActividadesPanel);
        totalTareasFlipPanel = new FlipPanel(totalTareasPanel, detalleTareasPanel);
        tareasObligatoriasFlipPanel = new FlipPanel(tareasObligatoriasPanel, detalleTareasObligatoriasPanel);

        // Crear botón de actualizar
        btnActualizar = new JButton("Actualizar Dashboard");
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(139, 92, 246));
        btnActualizar.setForeground(Color.black);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorder(BorderFactory.createLineBorder(new Color(167, 139, 250), 2));
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efectos hover para el botón
        btnActualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnActualizar.setBackground(new Color(167, 139, 250));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnActualizar.setBackground(new Color(139, 92, 246));
            }
        });

        // Agregar acción al botón
        btnActualizar.addActionListener(e -> actualizarEstadisticas());

        // Panel de gráficos
        chartsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        chartsPanel.setBackground(new Color(111, 63, 182));
    }

    private void setupLayout() {
        // Añadir FlipPanels al panel de estadísticas
        statsPanel.add(totalProcesosFlipPanel);
        statsPanel.add(totalActividadesFlipPanel);
        statsPanel.add(totalTareasFlipPanel);
        statsPanel.add(tareasObligatoriasFlipPanel);

        // Crear panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(111, 63, 182));

        // Panel superior para título y botón
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(111, 63, 182));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Título del dashboard
        JLabel titleLabel = new JLabel("Dashboard de Gestión", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Agregar título y botón al panel superior
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(btnActualizar, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        mainPanel.add(chartsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void actualizarEstadisticas() {
        Map<UUID, Proceso> procesos = gestionProcesos.getProcesos();

        int totalProcesos = procesos.size();
        int totalActividades = 0;
        int totalTareas = 0;
        int tareasObligatorias = 0;

        StringBuilder detalleProcesos = new StringBuilder();
        StringBuilder detalleActividades = new StringBuilder();
        StringBuilder detalleTareas = new StringBuilder();
        StringBuilder detalleTareasObligatorias = new StringBuilder();

        // Calcular estadísticas y preparar detalles
        int p = 1;
        int a = 1;
        int t = 1;

        for (Proceso proceso : procesos.values()) {
            detalleProcesos.append(p + ". ").append(proceso.getNombre()).append("\n");
            System.out.println("Proceso " + p + ": " + proceso.getNombre());  // Debug
            p++;

            ListaEnlazada<Actividad> listaActividades = proceso.getActividades();
            if (listaActividades != null && !listaActividades.estaVacia()) {
                Nodo<Actividad> actualActividad = listaActividades.getCabeza();

                while (actualActividad != null) {
                    totalActividades++;
                    Actividad actividad = actualActividad.getValorNodo();
                    detalleActividades.append(a + ". ").append(actividad.getNombre())
                            .append(" (").append(proceso.getNombre()).append(")\n");
                    System.out.println("  Actividad " + a + ": " + actividad.getNombre());  // Debug
                    a++;

                    Cola<Tarea> colaTareas = actividad.getTareas();
                    if (colaTareas != null && !colaTareas.estaVacia()) {
                        Nodo<Tarea> actualTarea = colaTareas.getNodoPrimero();

                        while (actualTarea != null) {
                            totalTareas++;
                            Tarea tarea = actualTarea.getValorNodo();
                            detalleTareas.append(t + ". ").append(tarea.getNombre())
                                    .append(" (").append(actividad.getNombre()).append(")\n");
                            System.out.println("    Tarea " + t + ": " + tarea.getNombre());

                            if (tarea.isObligatoria()) {
                                tareasObligatorias++;
                                detalleTareasObligatorias.append(t + ". ").append(tarea.getNombre())
                                        .append(" (").append(actividad.getNombre()).append(")\n");
                                System.out.println("      Tarea Obligatoria " + t + ": " + tarea.getNombre());
                            }
                            t++;
                            actualTarea = actualTarea.getSiguienteNodo();
                        }
                    }
                    actualActividad = actualActividad.getSiguienteNodo();
                }
            }
        }

        int finalTotalActividades = totalActividades;
        int finalTotalTareas = totalTareas;
        int finalTareasObligatorias = tareasObligatorias;

        SwingUtilities.invokeLater(() -> {
            totalProcesosPanel.setValor(String.valueOf(totalProcesos));
            totalActividadesPanel.setValor(String.valueOf(finalTotalActividades));
            totalTareasPanel.setValor(String.valueOf(finalTotalTareas));
            tareasObligatoriasPanel.setValor(String.valueOf(finalTareasObligatorias));

            detalleProcesosPanel.setDetalle(detalleProcesos.toString());
            detalleActividadesPanel.setDetalle(detalleActividades.toString());
            detalleTareasPanel.setDetalle(detalleTareas.toString());
            detalleTareasObligatoriasPanel.setDetalle(detalleTareasObligatorias.toString());

        });
    }
}