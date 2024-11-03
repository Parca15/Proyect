package Interfaz;

import ModelosBase.Actividad;
import ModelosBase.Proceso;
import ModelosBase.Tarea;
import Funcionalidades.GestionProcesos;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

public class ProcesoTreePanel extends JPanel {
    private final GestionProcesos gestionProcesos;
    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode rootNode;
    private  JTextField searchField;
    private Timer updateTimer;
    private JPopupMenu popupMenu;
    private CustomTreeCellRenderer treeCellRenderer;

    public ProcesoTreePanel(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
        setLayout(new BorderLayout(5, 5));

        // Panel superior para búsqueda
        JPanel searchPanel = createSearchPanel();

        // Configurar el árbol y sus componentes
        rootNode = new DefaultMutableTreeNode("Procesos");
        treeModel = new DefaultTreeModel(rootNode);
        tree = createTree();

        // Configurar el panel de desplazamiento
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(300, 500));

        // Crear menú contextual
        createPopupMenu();

        // Agregar componentes al panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Inicializar componentes
        configurarBusqueda();
        configurarTimer();
        actualizarArbol();
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchPanel.add(new JLabel("Buscar: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }

    private JTree createTree() {
        JTree newTree = new JTree(treeModel);
        newTree.setShowsRootHandles(true);
        newTree.setRootVisible(true);
        treeCellRenderer = new CustomTreeCellRenderer();
        newTree.setCellRenderer(treeCellRenderer);
        newTree.addMouseListener(new TreeMouseListener());
        return newTree;
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        JMenuItem expandItem = new JMenuItem("Expandir todo");
        JMenuItem collapseItem = new JMenuItem("Colapsar todo");

        expandItem.addActionListener(e -> expandirTodo(tree, true));
        collapseItem.addActionListener(e -> expandirTodo(tree, false));

        popupMenu.add(expandItem);
        popupMenu.add(collapseItem);
    }

    private void configurarBusqueda() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscar();
            }
        });
    }

    private void configurarTimer() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        updateTimer = new Timer(5000, e -> actualizarArbol());
        updateTimer.start();
    }

    private void buscar() {
        String searchText = searchField.getText().toLowerCase().trim();
        treeCellRenderer.setSearchText(searchText);
        if (searchText.isEmpty()) {
            expandirTodo(tree, false);
        } else {
            expandirTodo(tree, false);
            buscarEnNodo(rootNode, searchText);
        }
        tree.repaint();
    }

    private boolean buscarEnNodo(DefaultMutableTreeNode node, String searchText) {
        boolean encontrado = false;
        Object userObject = node.getUserObject();

        if (coincideConBusqueda(userObject, searchText)) {
            encontrado = true;
        }

        Enumeration<?> children = node.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
            if (buscarEnNodo(childNode, searchText)) {
                encontrado = true;
            }
        }

        if (encontrado) {
            expandirHastaRaiz(node);
        }

        return encontrado;
    }

    private boolean coincideConBusqueda(Object userObject, String searchText) {
        if (userObject instanceof String) {
            return ((String) userObject).toLowerCase().contains(searchText);
        } else if (userObject instanceof ProcesoNode) {
            return ((ProcesoNode) userObject).toString().toLowerCase().contains(searchText);
        } else if (userObject instanceof ActividadNode) {
            return ((ActividadNode) userObject).toString().toLowerCase().contains(searchText);
        } else if (userObject instanceof TareaNode) {
            return ((TareaNode) userObject).toString().toLowerCase().contains(searchText);
        }
        return false;
    }

    public void actualizarArbol() {
        rootNode.removeAllChildren();
        Map<UUID, Proceso> procesos = gestionProcesos.getProcesos();

        for (Proceso proceso : procesos.values()) {
            DefaultMutableTreeNode procesoNode = new DefaultMutableTreeNode(new ProcesoNode(proceso));
            agregarActividadesAlNodo(procesoNode, proceso);
            rootNode.add(procesoNode);
        }

        treeModel.reload();
        buscar(); // Mantener el estado de búsqueda
    }

    private void agregarActividadesAlNodo(DefaultMutableTreeNode procesoNode, Proceso proceso) {
        for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
            Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
            DefaultMutableTreeNode actividadNode = new DefaultMutableTreeNode(new ActividadNode(actividad));
            agregarTareasAlNodo(actividadNode, actividad);
            procesoNode.add(actividadNode);
        }
    }

    private void agregarTareasAlNodo(DefaultMutableTreeNode actividadNode, Actividad actividad) {
        for (var nodoTarea = actividad.getTareas().getNodoPrimero();
             nodoTarea != null;
             nodoTarea = nodoTarea.getSiguienteNodo()) {
            DefaultMutableTreeNode tareaNode = new DefaultMutableTreeNode(
                    new TareaNode(nodoTarea.getValorNodo())
            );
            actividadNode.add(tareaNode);
        }
    }

    private void expandirHastaRaiz(DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        tree.expandPath(path);
    }

    private void expandirTodo(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandirTodo(tree, new TreePath(root), expand);
    }

    private void expandirTodo(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
            TreeNode n = (TreeNode) e.nextElement();
            TreePath path = parent.pathByAddingChild(n);
            expandirTodo(tree, path, expand);
        }

        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    private class TreeMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopupMenu(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopupMenu(e);
            }
        }

        private void showPopupMenu(MouseEvent e) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    // Clases auxiliares para los nodos
    private static class ProcesoNode {
        private final Proceso proceso;

        public ProcesoNode(Proceso proceso) {
            this.proceso = proceso;
        }

        @Override
        public String toString() {
            return "Proceso: " + proceso.getNombre() + " (ID: " + proceso.getId() + ")";
        }
    }

    private static class ActividadNode {
        private final Actividad actividad;

        public ActividadNode(Actividad actividad) {
            this.actividad = actividad;
        }

        @Override
        public String toString() {
            return "Actividad: " + actividad.getNombre() +
                    (actividad.isObligatoria() ? " (Obligatoria)" : " (Opcional)");
        }
    }

    private static class TareaNode {
        private final Tarea tarea;

        public TareaNode(Tarea tarea) {
            this.tarea = tarea;
        }

        @Override
        public String toString() {
            return "Tarea: " + tarea.getDescripcion() +
                    " (" + tarea.getDuracion() + " min)" +
                    (tarea.isObligatoria() ? " (Obligatoria)" : " (Opcional)");
        }
    }

    private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        private String searchText = "";

        public void setSearchText(String text) {
            this.searchText = text.toLowerCase().trim();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            String text = userObject.toString();

            // Crear un JLabel personalizado para el resaltado
            JLabel label = new JLabel();
            label.setOpaque(true);

            // Configurar colores basados en la selección
            if (selected) {
                label.setBackground(getBackgroundSelectionColor());
                label.setForeground(getTextSelectionColor());
            } else {
                label.setBackground(getBackgroundNonSelectionColor());
                label.setForeground(getTextNonSelectionColor());
            }

            // Configurar el icono basado en el tipo de nodo
            Icon icon = null;
            if (userObject instanceof ProcesoNode) {
                icon = UIManager.getIcon("FileView.directoryIcon");
            } else if (userObject instanceof ActividadNode) {
                icon = UIManager.getIcon("FileView.fileIcon");
            } else if (userObject instanceof TareaNode) {
                icon = UIManager.getIcon("FileView.computerIcon");
            }
            label.setIcon(icon);

            // Resaltar el texto si hay una búsqueda activa
            if (!searchText.isEmpty() && text.toLowerCase().contains(searchText)) {
                label.setText(createHighlightedText(text, searchText));
            } else {
                label.setText(text);
            }

            return label;
        }

        private String createHighlightedText(String text, String searchText) {
            if (searchText.isEmpty()) return text;

            // Usar HTML para resaltar el texto
            StringBuilder result = new StringBuilder("<html>");
            String lowerText = text.toLowerCase();
            int searchLength = searchText.length();
            int currentIndex = 0;

            while (true) {
                int foundIndex = lowerText.indexOf(searchText, currentIndex);
                if (foundIndex == -1) {
                    // Agregar el resto del texto sin resaltar
                    result.append(text.substring(currentIndex));
                    break;
                }

                // Agregar el texto antes del término de búsqueda
                result.append(text.substring(currentIndex, foundIndex));
                // Agregar el término de búsqueda resaltado
                result.append("<span style='background-color: #FFFF00'>");
                result.append(text.substring(foundIndex, foundIndex + searchLength));
                result.append("</span>");

                currentIndex = foundIndex + searchLength;
            }

            result.append("</html>");
            return result.toString();
        }
    }

}