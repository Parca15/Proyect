package Interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FlipPanel extends JPanel {
    private final JPanel frontPanel;
    private final JPanel backPanel;
    private boolean isFlipped = false;
    private final CardLayout cardLayout;
    private Component lastClickedComponent;

    public FlipPanel(JPanel front, JPanel back) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        this.frontPanel = front;
        this.backPanel = back;

        setBackground(new Color(139, 92, 246));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(frontPanel, "front");
        add(backPanel, "back");

        cardLayout.show(this, "front");

        // Agregar listeners solo a los componentes específicos
        setupListeners();
    }

    private void setupListeners() {
        // Agregar listener a los componentes del panel frontal
        for (Component component : frontPanel.getComponents()) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!isFlipped) {
                        isFlipped = true;
                        cardLayout.show(FlipPanel.this, "back");
                        lastClickedComponent = e.getComponent();
                    }
                }
            });
        }

        // Para el panel trasero, buscamos específicamente el JTextArea dentro del DetalleEstadisticaPanel
        if (backPanel instanceof DetalleEstadisticaPanel) {
            DetalleEstadisticaPanel detallePanel = (DetalleEstadisticaPanel) backPanel;
            // Buscamos el JTextArea dentro del ScrollPane
            Component[] components = detallePanel.getComponents();
            for (Component component : components) {
                if (component instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) component;
                    JViewport viewport = scrollPane.getViewport();
                    Component[] viewComponents = viewport.getComponents();
                    for (Component viewComponent : viewComponents) {
                        if (viewComponent instanceof JTextArea) {
                            JTextArea textArea = (JTextArea) viewComponent;
                            textArea.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    if (isFlipped) {
                                        isFlipped = false;
                                        cardLayout.show(FlipPanel.this, "front");
                                        lastClickedComponent = null;
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public void reset() {
        isFlipped = false;
        lastClickedComponent = null;
        cardLayout.show(this, "front");
    }
}