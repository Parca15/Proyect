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

    private JPanel totalProcesosPanel;
    private JPanel totalActividadesPanel;
    private JPanel totalTareasPanel;
    private JPanel tareasObligatoriasPanel;

    private JLabel totalProcesosValor;
    private JLabel totalActividadesValor;
    private JLabel totalTareasValor;
    private JLabel tareasObligatoriasValor;
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

        // Crear paneles individuales para cada estadística
        totalProcesosPanel = crearPanelEstadistica("Total Procesos");
        totalActividadesPanel = crearPanelEstadistica("Total Actividades");
        totalTareasPanel = crearPanelEstadistica("Total Tareas");
        tareasObligatoriasPanel = crearPanelEstadistica("Tareas Obligatorias");

        // Obtener referencias a las etiquetas de valores
        totalProcesosValor = (JLabel) totalProcesosPanel.getComponent(1);
        totalActividadesValor = (JLabel) totalActividadesPanel.getComponent(1);
        totalTareasValor = (JLabel) totalTareasPanel.getComponent(1);
        tareasObligatoriasValor = (JLabel) tareasObligatoriasPanel.getComponent(1);

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

    private JPanel crearPanelEstadistica(String titulo) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(new Color(139, 92, 246));
        panel.setBorder(BorderFactory.createLineBorder(new Color(167, 139, 250), 2));

        // Etiqueta del título
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Etiqueta del valor
        JLabel valorLabel = new JLabel("0");
        valorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valorLabel.setForeground(Color.WHITE);
        valorLabel.setFont(new Font("Arial", Font.BOLD, 24));

        panel.add(tituloLabel);
        panel.add(valorLabel);

        return panel;
    }

    private void setupLayout() {
        // Añadir componentes al panel de estadísticas
        statsPanel.add(totalProcesosPanel);
        statsPanel.add(totalActividadesPanel);
        statsPanel.add(totalTareasPanel);
        statsPanel.add(tareasObligatoriasPanel);

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

        // Calcular estadísticas
        for (Proceso proceso : procesos.values()) {
            ListaEnlazada<Actividad> listaActividades = proceso.getActividades();
            if (listaActividades != null && !listaActividades.estaVacia()) {
                Nodo<Actividad> actualActividad = listaActividades.getCabeza();

                while (actualActividad != null) {
                    totalActividades++;
                    Actividad actividad = actualActividad.getValorNodo();

                    // Contar tareas de esta actividad
                    Cola<Tarea> colaTareas = actividad.getTareas();
                    if (colaTareas != null && !colaTareas.estaVacia()) {
                        Nodo<Tarea> actualTarea = colaTareas.getNodoPrimero();

                        while (actualTarea != null) {
                            totalTareas++;
                            if (actualTarea.getValorNodo().isObligatoria()) {
                                tareasObligatorias++;
                            }
                            actualTarea = actualTarea.getSiguienteNodo();
                        }
                    }

                    actualActividad = actualActividad.getSiguienteNodo();
                }
            }
        }

        // Actualizar las etiquetas en el EDT
        final int finalTotalActividades = totalActividades;
        final int finalTotalTareas = totalTareas;
        final int finalTareasObligatorias = tareasObligatorias;

        SwingUtilities.invokeLater(() -> {
            totalProcesosValor.setText(String.valueOf(totalProcesos));
            totalActividadesValor.setText(String.valueOf(finalTotalActividades));
            totalTareasValor.setText(String.valueOf(finalTotalTareas));
            tareasObligatoriasValor.setText(String.valueOf(finalTareasObligatorias));

            // Mostrar un mensaje de confirmación
            JOptionPane.showMessageDialog(this,
                    "Dashboard actualizado correctamente",
                    "Actualización Completada",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }
}