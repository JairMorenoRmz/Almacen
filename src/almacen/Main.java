package almacen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        LoggerConfig.setupLogger();

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:inventario.db");
            System.out.println("Conexión establecida.");  
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (conn == null) {
            System.err.println("Connection failed to initialize.");
            return;
        }

        Usuario usuario = new Usuario(conn);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Iniciando LoginFrame..."); 
                LoginFrame loginFrame = new LoginFrame(conn, usuario);
                loginFrame.setVisible(true);
            }
        });
    }
}
