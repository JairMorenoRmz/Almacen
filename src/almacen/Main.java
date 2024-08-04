package almacen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:inventario.db");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        Almacen almacen = new Almacen(conn);
        Usuario usuario = new Usuario(conn);
        Scanner scanner = new Scanner(System.in);
        String currentUser = null;
        String rol = null;

        while (currentUser == null) {
            System.out.println("\nIniciar Sesión");
            System.out.print("Usuario: ");
            String username = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();
            rol = usuario.verificarUsuario(username, password);
            if (rol != null) {
                currentUser = username;
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
                usuario.crearUsuario(nuevoUsuario, nuevaContraseña, nuevoRol);
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
