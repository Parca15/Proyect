package Interfaz;

import Funcionalidades.*;
import ModelosBase.Proceso;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class MainApplication extends JFrame {
    private final GestionProcesos gestionProcesos;
    private final ExcelDataHandler excelHandler;
    private ProcesoTreePanel procesoTreePanel;
    private ProcesoPanel procesoPanel;

    public MainApplication() {
        super("Sistema de Gestión de Procesos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Definir colores del tema morado
        Color colorFondoPrincipal = new Color(111, 63, 182);        // Morado principal
        Color colorSecundario = new Color(139, 92, 246);           // Morado más claro
        Color colorAccent = new Color(167, 139, 250);             // Morado claro/lavanda
        Color colorTexto = Color.BLACK;                           // Texto negro

        // Inicializar los gestores
        gestionProcesos = new GestionProcesos(new HashMap<UUID, Proceso>());
        excelHandler = new ExcelDataHandler(gestionProcesos);
        GestionActividades gestionActividades = new GestionActividades(gestionProcesos);
        GestionTareas gestionTareas = new GestionTareas(gestionProcesos);
        GestionConsultas gestionConsultas = new GestionConsultas(gestionProcesos);

        // Crear el panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(colorFondoPrincipal);

        // Crear barra de menú
        JMenuBar menuBar = crearMenuBar(colorSecundario, colorTexto);
        setJMenuBar(menuBar);

        // Panel izquierdo para procesos
        procesoPanel = new ProcesoPanel(gestionProcesos);
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

        // Panel central para pestañas
        JTabbedPane centerTabbedPane = new JTabbedPane();
        centerTabbedPane.setBackground(colorSecundario);
        centerTabbedPane.setForeground(colorTexto);

        // Crear e inicializar paneles
        DashboardPanel dashboardPanel = new DashboardPanel(gestionProcesos);
        dashboardPanel.setBackground(colorFondoPrincipal);
        ActividadPanel actividadPanel = new ActividadPanel(gestionActividades, procesoPanel);
        TareaPanel tareaPanel = new TareaPanel(gestionTareas, gestionConsultas, procesoPanel);

        // Crear el nuevo ProcesoTreePanel
        procesoTreePanel = new ProcesoTreePanel(gestionProcesos);
        procesoTreePanel.setBackground(colorFondoPrincipal);

        // Personalizar los paneles
        actividadPanel.setBackground(colorFondoPrincipal);
        tareaPanel.setBackground(colorFondoPrincipal);

        // Agregar las pestañas
        centerTabbedPane.addTab("Actividades", actividadPanel);
        centerTabbedPane.addTab("Tareas", tareaPanel);
        centerTabbedPane.addTab("Árbol de Procesos", new ImageIcon(), procesoTreePanel, "Vista jerárquica de procesos");
        centerTabbedPane.addTab("Dashboard", new ImageIcon(), dashboardPanel, "Vista general del sistema");

        // Agregar bordes
        mainPanel.setBorder(BorderFactory.createLineBorder(colorAccent, 2));
        leftPanel.setBorder(BorderFactory.createLineBorder(colorAccent, 1));

        // Efecto hover en pestañas
        centerTabbedPane.addChangeListener(e -> {
            int selectedIndex = centerTabbedPane.getSelectedIndex();
            for (int i = 0; i < centerTabbedPane.getTabCount(); i++) {
                if (i == selectedIndex) {
                    centerTabbedPane.setBackgroundAt(i, colorAccent);
                } else {
                    centerTabbedPane.setBackgroundAt(i, colorSecundario);
                }
            }

            if (selectedIndex == 3) {
                dashboardPanel.actualizarEstadisticas();
            } else if (selectedIndex == 2) {
                procesoTreePanel.actualizarArbol();
            }
        });
        centerTabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Agregar paneles al mainPanel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerTabbedPane, BorderLayout.CENTER);

        // Toolbar para acciones rápidas
        JToolBar toolBar = crearToolBar(colorSecundario);
        mainPanel.add(toolBar, BorderLayout.NORTH);

        setContentPane(mainPanel);

        // Aplicar tema del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JMenuBar crearMenuBar(Color colorSecundario, Color colorTexto) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(colorSecundario);

        JMenu archivoMenu = new JMenu("Archivo");
        archivoMenu.setForeground(colorTexto);

        JMenuItem exportarMenuItem = new JMenuItem("Exportar a Excel");
        JMenuItem importarMenuItem = new JMenuItem("Importar desde Excel");

        exportarMenuItem.addActionListener(e -> exportarDatos());
        importarMenuItem.addActionListener(e -> importarDatos());

        archivoMenu.add(exportarMenuItem);
        archivoMenu.add(importarMenuItem);
        menuBar.add(archivoMenu);

        return menuBar;
    }

    private JToolBar crearToolBar(Color colorSecundario) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(colorSecundario);

        JButton exportarButton = new JButton("Exportar");
        JButton importarButton = new JButton("Importar");

        exportarButton.addActionListener(e -> exportarDatos());
        importarButton.addActionListener(e -> importarDatos());

        toolBar.add(exportarButton);
        toolBar.add(importarButton);

        return toolBar;
    }

    private void actualizarListaProcesos() {
        // Obtener el modelo de la lista del ProcesoPanel
        DefaultListModel<String> model = procesoPanel.getListModel();
        model.clear(); // Limpiar la lista actual

        // Obtener los procesos actualizados y añadirlos al modelo
        for (Proceso proceso : gestionProcesos.getProcesos().values()) {
            model.addElement(proceso.getNombre() + " (" + proceso.getId() + ")");
        }
    }

    private void exportarDatos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar datos a Excel");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".xlsx") || f.isDirectory();
            }

            public String getDescription() {
                return "Archivos Excel (*.xlsx)";
            }
        });

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            try {
                excelHandler.exportarProcesoSeleccionado(filePath, procesoPanel.getSelectedProcesoId());
                JOptionPane.showMessageDialog(this,
                        "Datos exportados exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al exportar los datos: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarDatos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar datos desde Excel");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".xlsx") || f.isDirectory();
            }

            public String getDescription() {
                return "Archivos Excel (*.xlsx)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                excelHandler.importarDatos(fileChooser.getSelectedFile().getAbsolutePath());
                // Actualizar la lista de procesos
                actualizarListaProcesos();

                JOptionPane.showMessageDialog(this,
                        "Datos importados exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                // Actualizar otros componentes de la interfaz
                procesoTreePanel.actualizarArbol();
                repaint();
                revalidate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al importar los datos: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
    }
}