package almacen;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Almacen {
    private Connection conn;
    private Statement stmt;

    public Almacen() {
        try {
            // Establecer la conexión a la base de datos SQLite
            conn = DriverManager.getConnection("jdbc:sqlite:inventario.db");
            stmt = conn.createStatement();
            String createInventarioTable = "CREATE TABLE IF NOT EXISTS inventario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "producto TEXT," +
                    "peso REAL," +
                    "ubicacion TEXT," +
                    "fecha TEXT," +
                    "tipo TEXT)";
            stmt.execute(createInventarioTable);
            String createUsuariosTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT," +
                    "rol TEXT)";
            stmt.execute(createUsuariosTable);
            crearUsuarioInicial();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void crearUsuarioInicial() {
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
        try {
            String insertUser = "INSERT INTO usuarios (username, password, rol) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertUser);
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
        try {
            String query = "SELECT rol FROM usuarios WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("rol");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void entradaProducto(String producto, ArrayList<Double> pesos, String ubicacion) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try {
            String insertProducto = "INSERT INTO inventario (producto, peso, ubicacion, fecha, tipo) VALUES (?, ?, ?, ?, 'entrada')";
            PreparedStatement pstmt = conn.prepareStatement(insertProducto);
            for (Double peso : pesos) {
                pstmt.setString(1, producto);
                pstmt.setDouble(2, peso);
                pstmt.setString(3, ubicacion);
                pstmt.setString(4, fecha);
                pstmt.executeUpdate();
            }
            System.out.println("Entrada: " + pesos.size() + " unidades de " + producto + " en " + ubicacion + ". Fecha: " + fecha);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void salidaProducto(String producto, ArrayList<Double> pesos, String ubicacion) {
        try {
            for (Double peso : pesos) {
                String query = "SELECT id, peso FROM inventario WHERE producto = ? AND ubicacion = ? AND tipo = 'entrada' ORDER BY fecha";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, producto);
                pstmt.setString(2, ubicacion);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    String insertSalida = "INSERT INTO inventario (producto, peso, ubicacion, fecha, tipo) VALUES (?, ?, ?, ?, 'salida')";
                    pstmt = conn.prepareStatement(insertSalida);
                    pstmt.setString(1, producto);
                    pstmt.setDouble(2, rs.getDouble("peso"));
                    pstmt.setString(3, ubicacion);
                    pstmt.setString(4, fecha);
                    pstmt.executeUpdate();

                    String deleteEntrada = "DELETE FROM inventario WHERE id = ?";
                    pstmt = conn.prepareStatement(deleteEntrada);
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();

                    System.out.println("Salida: " + producto + " en " + ubicacion + ". Fecha: " + fecha);
                } else {
                    System.out.println("No hay suficiente " + producto + " en inventario en " + ubicacion + " para la salida solicitada.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mostrarInventario() {
        System.out.println("\nInventario actual:");
        try {
            String query = "SELECT producto, ubicacion, COUNT(*), SUM(peso), MAX(fecha) FROM inventario WHERE tipo = 'entrada' GROUP BY producto, ubicacion ORDER BY ubicacion";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String producto = rs.getString(1);
                String ubicacion = rs.getString(2);
                int cantidad = rs.getInt(3);
                double totalPeso = rs.getDouble(4);
                String fecha = rs.getString(5);
                System.out.println("Ubicación: " + ubicacion);
                System.out.println("  " + producto + ": " + cantidad + " unidades, " + totalPeso + " kg, Última entrada: " + fecha);

                query = "SELECT peso, fecha FROM inventario WHERE producto = ? AND ubicacion = ? AND tipo = 'entrada'";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, producto);
                pstmt.setString(2, ubicacion);
                ResultSet pesosFechas = pstmt.executeQuery();
                while (pesosFechas.next()) {
                    System.out.println("  Peso: " + pesosFechas.getDouble("peso") + " kg, Fecha: " + pesosFechas.getString("fecha"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mostrarHistorial() {
        System.out.println("\nHistorial de Entradas y Salidas:");
        try {
            String query = "SELECT id, producto, peso, ubicacion, fecha, tipo FROM inventario";
            ResultSet rs = stmt.executeQuery(query);
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
        try {
            String deleteQuery = "DELETE FROM inventario WHERE fecha < ?";
            PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
            pstmt.setString(1, fechaLimite);
            pstmt.executeUpdate();
            System.out.println("Materiales eliminados del historial antes de la fecha " + fechaLimite);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarHistorialManual(int id) {
        try {
            String deleteQuery = "DELETE FROM inventario WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Material con ID " + id + " eliminado del historial.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Almacen almacen = new Almacen();
        Scanner scanner = new Scanner(System.in);
        String usuario = null;
        String rol = null;

        while (usuario == null) {
            System.out.println("\nIniciar Sesión");
            System.out.print("Usuario: ");
            String username = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();
            rol = almacen.verificarUsuario(username, password);
            if (rol != null) {
                usuario = username;
                System.out.println("Bienvenido, " + username + ". Rol: " + rol);
            } else {
                System.out.println("Credenciales incorrectas. Intenta nuevamente.");
            }
        }

        while (true) {
            System.out.println("\nGestión de Almacén");
            if (rol.equals("admin")) {
                System.out.println("1. Crear usuario");
            }
            if (rol.equals("admin") || rol.equals("planta1") || rol.equals("planta2")) {
                System.out.println("2. Entrada de producto");
                System.out.println("3. Salida de producto");
            }
            System.out.println("4. Mostrar inventario");
            if (rol.equals("admin")) {
                System.out.println("5. Historial de Entradas y Salidas");
                System.out.println("6. Eliminar materiales del historial");
                System.out.println("7. Eliminar manualmente un material del historial");
            }
            System.out.println("8. Salir");
            System.out.print("Selecciona una opción: ");
            String opcion = scanner.nextLine();

            if (opcion.equals("1") && rol.equals("admin")) {
                System.out.print("Introduce el nombre del nuevo usuario: ");
                String nuevoUsuario = scanner.nextLine();
                System.out.print("Introduce la contraseña del nuevo usuario: ");
                String nuevaContraseña = scanner.nextLine();
                System.out.println("Selecciona el rol del nuevo usuario:");
                System.out.println("1. Admin");
                System.out.println("2. Planta 1");
                System.out.println("3. Planta 2");
                System.out.println("4. Visualizador");
                String rolOpcion = scanner.nextLine();
                String nuevoRol = null;
                switch (rolOpcion) {
                    case "1":
                        nuevoRol = "admin";
                        break;
                    case "2":
                        nuevoRol = "planta1";
                        break;
                    case "3":
                        nuevoRol = "planta2";
                        break;
                    case "4":
                        nuevoRol = "visualizador";
                        break;
                    default:
                        System.out.println("Rol no válido. Inténtalo de nuevo.");
                        continue;
                }
                almacen.crearUsuario(nuevoUsuario, nuevaContraseña, nuevoRol);
            } else if (opcion.equals("2") && (rol.equals("admin") || rol.equals("planta1") || rol.equals("planta2"))) {
                System.out.print("Introduce el nombre del producto: ");
                String producto = scanner.nextLine();
                ArrayList<Double> pesos = new ArrayList<>();
                while (true) {
                    System.out.print("Introduce el peso del material en kilogramos: ");
                    pesos.add(Double.parseDouble(scanner.nextLine()));
                    System.out.print("¿Agregar más? (Si/No): ");
                    String mas = scanner.nextLine().trim().toLowerCase();
                    if (!mas.equals("si")) {
                        break;
                    }
                }
                System.out.println("Selecciona la ubicación del almacén:");
                if (rol.equals("admin") || rol.equals("planta1")) {
                    System.out.println("1. Planta 1 - Almacén Central");
                    System.out.println("2. Planta 1 - Almacén Intermedio");
                    System.out.println("3. Planta 1 - Almacén Temporal");
                }
                if (rol.equals("admin") || rol.equals("planta2")) {
                    System.out.println("4. Planta 2 - Almacén Único");
                }
                String ubicacionOpcion = scanner.nextLine();
                String ubicacion = null;
                if (ubicacionOpcion.equals("1") && (rol.equals("admin") || rol.equals("planta1"))) {
                    ubicacion = "Planta 1 - Almacén Central";
                } else if (ubicacionOpcion.equals("2") && (rol.equals("admin") || rol.equals("planta1"))) {
                    ubicacion = "Planta 1 - Almacén Intermedio";
                } else if (ubicacionOpcion.equals("3") && (rol.equals("admin") || rol.equals("planta1"))) {
                    ubicacion = "Planta 1 - Almacén Temporal";
                } else if (ubicacionOpcion.equals("4") && (rol.equals("admin") || rol.equals("planta2"))) {
                    ubicacion = "Planta 2 - Almacén Único";
                } else {
                    System.out.println("Ubicación no válida o no tienes permisos para esta acción. Inténtalo de nuevo.");
                    continue;
                }
                almacen.entradaProducto(producto, pesos, ubicacion);
            } else if (opcion.equals("3") && (rol.equals("admin") || rol.equals("planta1") || rol.equals("planta2"))) {
                System.out.print("Introduce el nombre del producto: ");
                String producto = scanner.nextLine();
                ArrayList<Double> pesos = new ArrayList<>();
                while (true) {
                    System.out.print("Introduce el peso del material en kilogramos para la salida: ");
                    pesos.add(Double.parseDouble(scanner.nextLine()));
                    System.out.print("¿Dar de baja más del mismo material? (Si/No): ");
                    String mas = scanner.nextLine().trim().toLowerCase();
                    if (!mas.equals("si")) {
                        break;
                    }
                }
                System.out.println("Selecciona la ubicación del almacén:");
                if (rol.equals("admin") || rol.equals("planta1")) {
                    System.out.println("1. Planta 1 - Almacén Central");
                    System.out.println("2. Planta 1 - Almacén Intermedio");
                    System.out.println("3. Planta 1 - Almacén Temporal");
                }
                if (rol.equals("admin") || rol.equals("planta2")) {
                    System.out.println("4. Planta 2 - Almacén Único");
                }
                String ubicacionOpcion = scanner.nextLine();
                String ubicacion = null;
                if (ubicacionOpcion.equals("1") && (rol.equals("admin") || rol.equals("planta1"))) {
                    ubicacion = "Planta 1 - Almacén Central";
                } else if (ubicacionOpcion.equals("2") && (rol.equals("admin") || rol.equals("planta1"))) {
                    ubicacion = "Planta 1 - Almacén Intermedio";
                } else if (ubicacionOpcion.equals("3") && (rol.equals("admin") || rol.equals("planta1"))) {
                    ubicacion = "Planta 1 - Almacén Temporal";
                } else if (ubicacionOpcion.equals("4") && (rol.equals("admin") || rol.equals("planta2"))) {
                    ubicacion = "Planta 2 - Almacén Único";
                } else {
                    System.out.println("Ubicación no válida o no tienes permisos para esta acción. Inténtalo de nuevo.");
                    continue;
                }
                almacen.salidaProducto(producto, pesos, ubicacion);
            } else if (opcion.equals("4")) {
                almacen.mostrarInventario();
            } else if (opcion.equals("5") && rol.equals("admin")) {
                almacen.mostrarHistorial();
            } else if (opcion.equals("6") && rol.equals("admin")) {
                almacen.eliminarHistorialAntiguo();
            } else if (opcion.equals("7") && rol.equals("admin")) {
                System.out.print("Introduce el ID del material a eliminar del historial: ");
                int id = Integer.parseInt(scanner.nextLine());
                almacen.eliminarHistorialManual(id);
            } else if (opcion.equals("8")) {
                System.out.println("Saliendo del programa.");
                break;
            } else {
                System.out.println("Opción no válida o no tienes permisos para esta acción. Inténtalo de nuevo.");
            }
        }
        scanner.close();
    }
}