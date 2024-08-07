package almacen;

import java.sql.*;

public class Usuario {
    private Connection conn;

    public Usuario(Connection conn) {
        this.conn = conn;
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

    public void eliminarUsuario(String username) {
        String deleteUser = "DELETE FROM usuarios WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteUser)) {
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario " + username + " eliminado con éxito.");
            } else {
                System.out.println("Usuario " + username + " no encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
