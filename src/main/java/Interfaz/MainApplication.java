package Interfaz;

import Funcionalidades.*;
import ModelosBase.Proceso;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class MainApplication extends JFrame {
    public MainApplication() {
        super("Sistema de Gestión de Procesos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Definir colores del tema morado
        Color colorFondoPrincipal = new Color(111, 63, 182);        // Morado principal
        Color colorSecundario = new Color(139, 92, 246);           // Morado más claro
        Color colorAccent = new Color(167, 139, 250);             // Morado claro/lavanda
        Color colorTexto = Color.BLACK;                           // Texto blanco

        // Inicializar los gestores
        GestionProcesos gestionProcesos = new GestionProcesos(new HashMap<UUID, Proceso>());
        GestionActividades gestionActividades = new GestionActividades(gestionProcesos);
        GestionTareas gestionTareas = new GestionTareas(gestionProcesos);
        GestionConsultas gestionConsultas = new GestionConsultas(gestionProcesos);

        // Crear el panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(colorFondoPrincipal);

        // Panel izquierdo para procesos
        ProcesoPanel procesoPanel = new ProcesoPanel(gestionProcesos);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(procesoPanel, BorderLayout.CENTER);
        leftPanel.setBackground(colorSecundario);
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Configurar el aspecto del TabbedPane
        UIManager.put("TabbedPane.selected", colorAccent);
        UIManager.put("TabbedPane.background", colorSecundario);
        UIManager.put("TabbedPane.foreground", colorTexto);
        UIManager.put("TabbedPane.selectedForeground", Color.white);
        UIManager.put("TabbedPane.contentAreaColor", colorFondoPrincipal);

        // Panel central para pestañas de dashboard, actividades y tareas
        JTabbedPane centerTabbedPane = new JTabbedPane();
        centerTabbedPane.setBackground(colorSecundario);
        centerTabbedPane.setForeground(colorTexto);

        // Crear e inicializar el panel de dashboard
        DashboardPanel dashboardPanel = new DashboardPanel(gestionProcesos);
        dashboardPanel.setBackground(colorFondoPrincipal);

        // Crear los paneles de actividades y tareas
        ActividadPanel actividadPanel = new ActividadPanel(gestionActividades, procesoPanel);
        TareaPanel tareaPanel = new TareaPanel(gestionTareas, gestionConsultas, procesoPanel);

        // Personalizar los paneles
        actividadPanel.setBackground(colorFondoPrincipal);
        tareaPanel.setBackground(colorFondoPrincipal);

        // Agregar las pestañas al TabbedPane
        centerTabbedPane.addTab("Dashboard", new ImageIcon(), dashboardPanel, "Vista general del sistema");
        centerTabbedPane.addTab("Actividades", actividadPanel);
        centerTabbedPane.addTab("Tareas", tareaPanel);

        // Agregar bordes con el color del tema
        mainPanel.setBorder(BorderFactory.createLineBorder(colorAccent, 2));
        leftPanel.setBorder(BorderFactory.createLineBorder(colorAccent, 1));

        // Agregar efecto hover a las pestañas
        centerTabbedPane.addChangeListener(e -> {
            int selectedIndex = centerTabbedPane.getSelectedIndex();
            for (int i = 0; i < centerTabbedPane.getTabCount(); i++) {
                if (i == selectedIndex) {
                    centerTabbedPane.setBackgroundAt(i, colorAccent);
                } else {
                    centerTabbedPane.setBackgroundAt(i, colorSecundario);
                }
            }

            // Actualizar estadísticas cuando se selecciona la pestaña del dashboard
            if (selectedIndex == 0) {
                dashboardPanel.actualizarEstadisticas();
            }
        });
        centerTabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Agregar paneles al mainPanel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerTabbedPane, BorderLayout.CENTER);

        // Agregar el panel principal al frame
        setContentPane(mainPanel);

        // Aplicar tema oscuro a los componentes del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}