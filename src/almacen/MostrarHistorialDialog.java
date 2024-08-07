package almacen;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class MostrarHistorialDialog extends JDialog {
    private Almacen almacen;
    private JTextArea resultArea;

    public MostrarHistorialDialog(JFrame parent, Almacen almacen) {
        super(parent, "Historial de Entradas y Salidas", true);
        this.almacen = almacen;

        setLayout(new BorderLayout());

        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(scrollPane, BorderLayout.CENTER);

        setSize(600, 400);
        setLocationRelativeTo(parent);
        mostrarHistorial();
    }

    private void mostrarHistorial() {
        resultArea.setText("");
        try {
            Connection conn = almacen.getConnection();
            if (conn == null) {
                resultArea.setText("No se pudo establecer conexión con la base de datos.");
                return;
            }
            String query = "SELECT * FROM inventario ORDER BY fecha DESC";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                ArrayList<String> results = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String producto = rs.getString("producto");
                    double peso = rs.getDouble("peso");
                    String ubicacion = rs.getString("ubicacion");
                    String fecha = rs.getString("fecha");
                    String tipo = rs.getString("tipo");

                    results.add(String.format("ID: %d, Producto: %s, Peso: %.2f kg, Ubicación: %s, Fecha: %s, Tipo: %s",
                            id, producto, peso, ubicacion, fecha, tipo));
                }
                if (results.isEmpty()) {
                    resultArea.setText("No se encontraron resultados.");
                } else {
                    for (String result : results) {
                        resultArea.append(result + "\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error al consultar la base de datos.");
        }
    }
}
