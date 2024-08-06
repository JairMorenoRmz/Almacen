package almacen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CrearUsuarioDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> rolComboBox;
    private JButton createButton;

    public CrearUsuarioDialog(JFrame parent, Usuario usuario) {
        super(parent, "Crear Usuario", true);
        setLayout(null);
        setSize(300, 200);
        setLocationRelativeTo(parent);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 10, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 10, 160, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 40, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 40, 160, 25);
        add(passwordField);

        JLabel rolLabel = new JLabel("Rol:");
        rolLabel.setBounds(10, 70, 80, 25);
        add(rolLabel);

        rolComboBox = new JComboBox<>(new String[]{"admin", "planta1", "planta2", "visualizador"});
        rolComboBox.setBounds(100, 70, 160, 25);
        add(rolComboBox);

        createButton = new JButton("Crear");
        createButton.setBounds(100, 110, 80, 25);
        add(createButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String rol = (String) rolComboBox.getSelectedItem();
                usuario.crearUsuario(username, password, rol);
                JOptionPane.showMessageDialog(CrearUsuarioDialog.this, "Usuario creado con éxito.");
                dispose();
            }
        });
    }
}
