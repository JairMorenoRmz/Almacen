package almacen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class MostrarInventarioDialog extends JDialog {
    private Almacen almacen;
    private JTextField searchField;
    private JButton searchButton;
    private JTextArea resultArea;

    public MostrarInventarioDialog(JFrame parent, Almacen almacen) {
        super(parent, "Mostrar Inventario", true);
        this.almacen = almacen;

        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("Buscar Material:");
        searchField = new JTextField(20);
        searchButton = new JButton("Buscar");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchQuery = searchField.getText();
                mostrarInventarioDetallado(searchQuery);
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setSize(600, 400);
        setLocationRelativeTo(parent);
        mostrarInventarioDetallado("");
    }

    private void mostrarInventarioDetallado(String filtro) {
        resultArea.setText("");
        try {
            Connection conn = almacen.getConnection();
            if (conn == null) {
                resultArea.setText("No se pudo establecer conexión con la base de datos.");
                return;
            }
            String query = "SELECT * FROM inventario WHERE producto LIKE ? ORDER BY ubicacion, producto";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, "%" + filtro + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error al consultar la base de datos.");
        }
    }
}

