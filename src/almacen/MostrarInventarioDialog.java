package almacen;

import javax.swing.*;
import java.awt.*;

public class MostrarInventarioDialog extends JDialog {
    private JTextArea textArea;

    public MostrarInventarioDialog(JFrame parent, Almacen almacen) {
        super(parent, "Mostrar Inventario", true);
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(parent);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        mostrarInventario(almacen);
    }

    private void mostrarInventario(Almacen almacen) {
        StringBuilder sb = new StringBuilder();
        sb.append("Inventario actual:\n\n");
        almacen.mostrarInventarioToString(sb);
        textArea.setText(sb.toString());
    }
}

