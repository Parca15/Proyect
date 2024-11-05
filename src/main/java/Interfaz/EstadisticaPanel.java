package Interfaz;

import javax.swing.*;
import java.awt.*;

public class EstadisticaPanel extends JPanel {

    private final JLabel tituloLabel;
    private final JLabel valorLabel;

    public EstadisticaPanel(String titulo) {
        setLayout(new GridLayout(2, 1));
        setBackground(new Color(139, 92, 246));
        setBorder(BorderFactory.createLineBorder(new Color(167, 139, 250), 2));

        // Etiqueta del título
        tituloLabel = new JLabel(titulo);
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Etiqueta del valor
        valorLabel = new JLabel("0");
        valorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valorLabel.setForeground(Color.WHITE);
        valorLabel.setFont(new Font("Arial", Font.BOLD, 24));

        add(tituloLabel);
        add(valorLabel);
    }

    public void setValor(String valor) {
        valorLabel.setText(valor);
    }
}