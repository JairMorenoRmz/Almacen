package almacen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CrearUsuarioDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> rolComboBox;
    private Usuario usuario;

    public CrearUsuarioDialog(JFrame parent, Usuario usuario) {
        super(parent, "Crear Usuario", true);
        this.usuario = usuario;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 30));
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Rol:"), gbc);

        gbc.gridx = 1;
        rolComboBox = new JComboBox<>(new String[]{"admin", "planta1", "planta2", "visualizador"});
        rolComboBox.setPreferredSize(new Dimension(200, 30));
        add(rolComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton crearButton = new JButton("Crear");
        crearButton.setPreferredSize(new Dimension(200, 40));
        add(crearButton, gbc);

        crearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String rol = (String) rolComboBox.getSelectedItem();
                usuario.crearUsuario(username, password, rol);
                dispose();
            }
        });

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}
