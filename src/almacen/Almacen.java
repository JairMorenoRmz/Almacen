package almacen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Almacen {
    private Connection conn;
    private Statement stmt;
    private static final Logger logger = Logger.getLogger(Almacen.class.getName());

    public Almacen(Connection conn) {
        this.conn = conn;
        try {
            stmt = conn.createStatement();
            String createInventarioTable = "CREATE TABLE IF NOT EXISTS inventario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "producto TEXT," +
                    "peso REAL," +
                    "ubicacion TEXT," +
                    "fecha TEXT," +
                    "tipo TEXT)";
            stmt.execute(createInventarioTable);
            logger.info("Tabla de inventario creada o existente verificada.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creando tabla de inventario", e);
        }
    }

    public void entradaProducto(String producto, ArrayList<Double> pesos, String ubicacion) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try {
            conn.setAutoCommit(false); 
            logger.info("Iniciando transacción para entrada de producto.");
            String insertProducto = "INSERT INTO inventario (producto, peso, ubicacion, fecha, tipo) VALUES (?, ?, ?, ?, 'entrada')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertProducto)) {
                for (Double peso : pesos) {
                    pstmt.setString(1, producto);
                    pstmt.setDouble(2, peso);
                    pstmt.setString(3, ubicacion);
                    pstmt.setString(4, fecha);
                    pstmt.executeUpdate();
                }
            }
            conn.commit(); 
            logger.info("Transacción de entrada de producto confirmada.");
            System.out.println("Entrada: " + pesos.size() + " unidades de " + producto + " en " + ubicacion + ". Fecha: " + fecha);
        } catch (SQLException e) {
            try {
                conn.rollback(); 
                logger.warning("Transacción revertida debido a un error.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true); 
                logger.info("Modo autocommit restaurado.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void salidaProducto(String producto, ArrayList<Double> pesos, String ubicacion) {
        try {
            conn.setAutoCommit(false); 
            logger.info("Iniciando transacción para salida de producto.");
            for (Double peso : pesos) {
                String query = "SELECT id, peso FROM inventario WHERE producto = ? AND ubicacion = ? AND tipo = 'entrada' ORDER BY fecha";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, producto);
                    pstmt.setString(2, ubicacion);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String insertSalida = "INSERT INTO inventario (producto, peso, ubicacion, fecha, tipo) VALUES (?, ?, ?, ?, 'salida')";
                        try (PreparedStatement pstmtSalida = conn.prepareStatement(insertSalida)) {
                            pstmtSalida.setString(1, producto);
                            pstmtSalida.setDouble(2, rs.getDouble("peso"));
                            pstmtSalida.setString(3, ubicacion);
                            pstmtSalida.setString(4, fecha);
                            pstmtSalida.executeUpdate();
                        }
                        String deleteEntrada = "DELETE FROM inventario WHERE id = ?";
                        try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteEntrada)) {
                            pstmtDelete.setInt(1, id);
                            pstmtDelete.executeUpdate();
                        }
                        logger.info("Producto salido correctamente.");
                        System.out.println("Salida: " + producto + " en " + ubicacion + ". Fecha: " + fecha);
                    } else {
                        System.out.println("No hay suficiente " + producto + " en inventario en " + ubicacion + " para la salida solicitada.");
                        logger.warning("No hay suficiente inventario para la salida solicitada.");
                    }
                }
            }
            conn.commit(); 
            logger.info("Transacción de salida de producto confirmada.");
        } catch (SQLException e) {
            try {
                conn.rollback(); 
                logger.warning("Transacción revertida debido a un error.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true); 
                logger.info("Modo autocommit restaurado.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void mostrarInventarioToString(StringBuilder sb) {
        String query = "SELECT producto, ubicacion, COUNT(*), SUM(peso), MAX(fecha) FROM inventario WHERE tipo = 'entrada' GROUP BY producto, ubicacion ORDER BY ubicacion";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String producto = rs.getString(1);
                String ubicacion = rs.getString(2);
                int cantidad = rs.getInt(3);
                double totalPeso = rs.getDouble(4);
                String fecha = rs.getString(5);
                sb.append("Ubicación: ").append(ubicacion).append("\n");
                sb.append("  ").append(producto).append(": ").append(cantidad).append(" unidades, ").append(totalPeso).append(" kg, Última entrada: ").append(fecha).append("\n");

                String subQuery = "SELECT peso, fecha FROM inventario WHERE producto = ? AND ubicacion = ? AND tipo = 'entrada'";
                try (PreparedStatement pstmt = conn.prepareStatement(subQuery)) {
                    pstmt.setString(1, producto);
                    pstmt.setString(2, ubicacion);
                    try (ResultSet pesosFechas = pstmt.executeQuery()) {
                        while (pesosFechas.next()) {
                            sb.append("    Peso: ").append(pesosFechas.getDouble("peso")).append(" kg, Fecha: ").append(pesosFechas.getString("fecha")).append("\n");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error mostrando inventario", e);
        }
    }

    public void mostrarHistorialToString(StringBuilder sb) {
        String query = "SELECT id, producto, peso, ubicacion, fecha, tipo FROM inventario";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id")).append(" | ").append(rs.getString("tipo").toUpperCase()).append(": ")
                        .append(rs.getString("producto")).append(", ").append(rs.getDouble("peso")).append(" kg, Ubicación: ").append(rs.getString("ubicacion"))
                        .append(", Fecha: ").append(rs.getString("fecha")).append("\n");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error mostrando historial", e);
        }
    }

    public void eliminarHistorialAntiguo() {
        System.out.println("\nEliminar materiales del historial:");
        String fechaLimite = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));
        String deleteQuery = "DELETE FROM inventario WHERE fecha < ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, fechaLimite);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Materiales eliminados del historial antes de la fecha " + fechaLimite + ": " + rowsDeleted);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarHistorialManual(int id) {
        String deleteQuery = "DELETE FROM inventario WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Material con ID " + id + " eliminado del historial. Filas afectadas: " + rowsDeleted);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

