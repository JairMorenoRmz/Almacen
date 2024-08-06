package almacen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Usuario usuario;
    private Connection conn;

    public LoginFrame(Connection conn, Usuario usuario) {
        this.conn = conn;
        this.usuario = usuario;

        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 10, 80, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 10, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 40, 160, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 80, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String rol = usuario.verificarUsuario(username, password);
                if (rol != null) {
                    JOptionPane.showMessageDialog(null, "Bienvenido " + username + "!");
                    dispose(); 
                    System.out.println("Creating MainFrame..."); 
                    MainFrame mainFrame = new MainFrame(conn, usuario, rol);
                    System.out.println("Showing MainFrame..."); 
                    mainFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Username o password Invalido", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
