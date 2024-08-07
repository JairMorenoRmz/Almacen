package almacen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:inventario.db");
            Usuario usuario = new Usuario(conn);
            LoginFrame loginFrame = new LoginFrame(conn, usuario);
            loginFrame.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
