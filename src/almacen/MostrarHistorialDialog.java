package almacen;

import javax.swing.*;
import java.awt.*;

public class MostrarHistorialDialog extends JDialog {
    private JTextArea textArea;

    public MostrarHistorialDialog(JFrame parent, Almacen almacen) {
        super(parent, "Mostrar Historial", true);
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(parent);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        mostrarHistorial(almacen);
    }

    private void mostrarHistorial(Almacen almacen) {
        StringBuilder sb = new StringBuilder();
        sb.append("Historial de Entradas y Salidas:\n\n");
        almacen.mostrarHistorialToString(sb);
        textArea.setText(sb.toString());
    }
}

