package almacen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SalidaProductoDialog extends JDialog {
    private JTextField productoField;
    private JTextField pesoField;
    private JComboBox<String> ubicacionComboBox;
    private Almacen almacen;

    public SalidaProductoDialog(JFrame parent, Almacen almacen) {
        super(parent, "Salida de Producto", true);
        this.almacen = almacen;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Producto:"), gbc);

        gbc.gridx = 1;
        productoField = new JTextField(20);
        productoField.setPreferredSize(new Dimension(200, 30));
        add(productoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Peso:"), gbc);

        gbc.gridx = 1;
        pesoField = new JTextField(20);
        pesoField.setPreferredSize(new Dimension(200, 30));
        add(pesoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Ubicación:"), gbc);

        gbc.gridx = 1;
        ubicacionComboBox = new JComboBox<>(new String[]{"Planta 1 - Almacén Central", "Planta 1 - Almacén Intermedio", "Planta 1 - Almacén Temporal", "Planta 2 - Almacén Único"});
        ubicacionComboBox.setPreferredSize(new Dimension(200, 30));
        add(ubicacionComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton salidaButton = new JButton("Registrar Salida");
        salidaButton.setPreferredSize(new Dimension(200, 40));
        add(salidaButton, gbc);

        salidaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String producto = productoField.getText();
                double peso = Double.parseDouble(pesoField.getText());
                String ubicacion = (String) ubicacionComboBox.getSelectedItem();
                almacen.salidaProducto(producto, peso, ubicacion);
                dispose();
            }
        });

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}
