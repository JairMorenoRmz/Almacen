package almacen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Usuario {
    private Connection conn;

    public Usuario(Connection conn) {
        this.conn = conn;
        try {
            Statement stmt = conn.createStatement();
            String createUsuariosTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT," +
                    "rol TEXT)";
            stmt.execute(createUsuariosTable);
            crearUsuarioInicial(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void crearUsuarioInicial(Statement stmt) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios");
            if (rs.next() && rs.getInt(1) == 0) {
                crearUsuario("admin", "admin123", "admin");
                System.out.println("Usuario administrador por defecto creado: admin / admin123");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void crearUsuario(String username, String password, String rol) {
        String insertUser = "INSERT INTO usuarios (username, password, rol) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, rol);
            pstmt.executeUpdate();
            System.out.println("Usuario " + username + " creado con éxito.");
        } catch (SQLException e) {
            System.out.println("El nombre de usuario ya existe. Intenta con otro nombre de usuario.");
        }
    }

    public String verificarUsuario(String username, String password) {
        String query = "SELECT rol FROM usuarios WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("rol");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

