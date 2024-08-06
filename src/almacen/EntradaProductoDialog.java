package almacen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class EntradaProductoDialog extends JDialog {
    private JTextField productoField;
    private JTextField pesoField;
    private JTextField ubicacionField;
    private JButton addButton;
    private JButton doneButton;
    private ArrayList<Double> pesos;

    public EntradaProductoDialog(JFrame parent, Almacen almacen) {
        super(parent, "Entrada de Producto", true);
        setLayout(null);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JLabel productoLabel = new JLabel("Producto:");
        productoLabel.setBounds(10, 10, 80, 25);
        add(productoLabel);

        productoField = new JTextField(20);
        productoField.setBounds(100, 10, 260, 25);
        add(productoField);

        JLabel pesoLabel = new JLabel("Peso:");
        pesoLabel.setBounds(10, 40, 80, 25);
        add(pesoLabel);

        pesoField = new JTextField(20);
        pesoField.setBounds(100, 40, 260, 25);
        add(pesoField);

        JLabel ubicacionLabel = new JLabel("Ubicación:");
        ubicacionLabel.setBounds(10, 70, 80, 25);
        add(ubicacionLabel);

        ubicacionField = new JTextField(20);
        ubicacionField.setBounds(100, 70, 260, 25);
        add(ubicacionField);

        addButton = new JButton("Agregar Peso");
        addButton.setBounds(100, 110, 120, 25);
        add(addButton);

        doneButton = new JButton("Hecho");
        doneButton.setBounds(240, 110, 120, 25);
        add(doneButton);

        pesos = new ArrayList<>();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double peso = Double.parseDouble(pesoField.getText());
                pesos.add(peso);
                pesoField.setText("");
                JOptionPane.showMessageDialog(EntradaProductoDialog.this, "Peso agregado.");
            }
        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String producto = productoField.getText();
                String ubicacion = ubicacionField.getText();
                almacen.entradaProducto(producto, pesos, ubicacion);
                JOptionPane.showMessageDialog(EntradaProductoDialog.this, "Producto ingresado al almacén.");
                dispose();
            }
        });
    }
}

