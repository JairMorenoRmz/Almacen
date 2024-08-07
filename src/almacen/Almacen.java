package almacen;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Almacen {
    private Connection conn;
    private static final Logger logger = Logger.getLogger(Almacen.class.getName());

    public Almacen(Connection conn) {
        this.conn = conn;
        try {
            Statement stmt = conn.createStatement();
            String createInventarioTable = "CREATE TABLE IF NOT EXISTS inventario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "producto TEXT," +
                    "peso REAL," +
                    "ubicacion TEXT," +
                    "fecha TEXT," +
                    "tipo TEXT)";
            stmt.execute(createInventarioTable);
            logger.log(Level.INFO, "Tabla de inventario creada o existente verificada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.conn;
    }

    public void entradaProducto(String producto, double peso, String ubicacion) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String insertProducto = "INSERT INTO inventario (producto, peso, ubicacion, fecha, tipo) VALUES (?, ?, ?, ?, 'entrada')";
        try (PreparedStatement pstmt = conn.prepareStatement(insertProducto)) {
            pstmt.setString(1, producto);
            pstmt.setDouble(2, peso);
            pstmt.setString(3, ubicacion);
            pstmt.setString(4, fecha);
            pstmt.executeUpdate();
            logger.log(Level.INFO, "Entrada de producto: {0}, peso: {1}, ubicacion: {2}, fecha: {3}", 
                       new Object[]{producto, peso, ubicacion, fecha});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void salidaProducto(String producto, double peso, String ubicacion) {
        try {
            String query = "SELECT id, peso FROM inventario WHERE producto = ? AND ubicacion = ? AND tipo = 'entrada' ORDER BY fecha";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, producto);
            pstmt.setString(2, ubicacion);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next() && peso > 0) {
                int id = rs.getInt("id");
                double pesoDisponible = rs.getDouble("peso");

                if (pesoDisponible <= peso) {
                    eliminarProducto(id);
                    peso -= pesoDisponible;
                } else {
                    actualizarProducto(id, pesoDisponible - peso);
                    peso = 0;
                }
            }

            if (peso > 0) {
                logger.log(Level.WARNING, "No hay suficiente {0} en inventario para la salida solicitada.", producto);
            } else {
                String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String insertSalida = "INSERT INTO inventario (producto, peso, ubicacion, fecha, tipo) VALUES (?, ?, ?, ?, 'salida')";
                pstmt = conn.prepareStatement(insertSalida);
                pstmt.setString(1, producto);
                pstmt.setDouble(2, peso);
                pstmt.setString(3, ubicacion);
                pstmt.setString(4, fecha);
                pstmt.executeUpdate();
                logger.log(Level.INFO, "Salida de producto: {0}, peso: {1}, ubicacion: {2}, fecha: {3}", 
                           new Object[]{producto, peso, ubicacion, fecha});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarProducto(int id) {
        String deleteProducto = "DELETE FROM inventario WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteProducto)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            logger.log(Level.INFO, "Producto eliminado con ID: {0}", id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizarProducto(int id, double nuevoPeso) {
        String updateProducto = "UPDATE inventario SET peso = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateProducto)) {
            pstmt.setDouble(1, nuevoPeso);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            logger.log(Level.INFO, "Producto actualizado con ID: {0}, nuevo peso: {1}", new Object[]{id, nuevoPeso});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarHistorialAntiguo() {
        String fechaLimite = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));
        String deleteQuery = "DELETE FROM inventario WHERE fecha < ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, fechaLimite);
            int rowsAffected = pstmt.executeUpdate();
            logger.log(Level.INFO, "Materiales eliminados del historial antes de la fecha {0}. Filas afectadas: {1}", 
                       new Object[]{fechaLimite, rowsAffected});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarHistorialManual(int id) {
        String deleteQuery = "DELETE FROM inventario WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            logger.log(Level.INFO, "Material con ID {0} eliminado del historial. Filas afectadas: {1}", 
                       new Object[]{id, rowsAffected});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


