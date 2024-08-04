package almacen;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Almacen {
    private Connection conn;
    private Statement stmt;

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void entradaProducto(String producto, ArrayList<Double> pesos, String ubicacion) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try {
            conn.setAutoCommit(false); 
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
            System.out.println("Entrada: " + pesos.size() + " unidades de " + producto + " en " + ubicacion + ". Fecha: " + fecha);
        } catch (SQLException e) {
            try {
                conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true); 
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void salidaProducto(String producto, ArrayList<Double> pesos, String ubicacion) {
        try {
            conn.setAutoCommit(false); 
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
                        System.out.println("Salida: " + producto + " en " + ubicacion + ". Fecha: " + fecha);
                    } else {
                        System.out.println("No hay suficiente " + producto + " en inventario en " + ubicacion + " para la salida solicitada.");
                    }
                }
            }
            conn.commit(); 
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void mostrarInventario() {
        System.out.println("\nInventario actual:");
        String query = "SELECT producto, ubicacion, COUNT(*), SUM(peso), MAX(fecha) FROM inventario WHERE tipo = 'entrada' GROUP BY producto, ubicacion ORDER BY ubicacion";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String producto = rs.getString(1);
                String ubicacion = rs.getString(2);
                int cantidad = rs.getInt(3);
                double totalPeso = rs.getDouble(4);
                String fecha = rs.getString(5);
                System.out.println("Ubicación: " + ubicacion);
                System.out.println("  " + producto + ": " + cantidad + " unidades, " + totalPeso + " kg, Última entrada: " + fecha);

                String subQuery = "SELECT peso, fecha FROM inventario WHERE producto = ? AND ubicacion = ? AND tipo = 'entrada'";
                try (PreparedStatement pstmt = conn.prepareStatement(subQuery)) {
                    pstmt.setString(1, producto);
                    pstmt.setString(2, ubicacion);
                    try (ResultSet pesosFechas = pstmt.executeQuery()) {
                        while (pesosFechas.next()) {
                            System.out.println("  Peso: " + pesosFechas.getDouble("peso") + " kg, Fecha: " + pesosFechas.getString("fecha"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mostrarHistorial() {
        System.out.println("\nHistorial de Entradas y Salidas:");
        String query = "SELECT id, producto, peso, ubicacion, fecha, tipo FROM inventario";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | " + rs.getString("tipo").toUpperCase() + ": " +
                        rs.getString("producto") + ", " + rs.getDouble("peso") + " kg, Ubicación: " + rs.getString("ubicacion") +
                        ", Fecha: " + rs.getString("fecha"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
