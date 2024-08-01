package com.mycompany.almacenapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class AlmacenTest {

    private AlmacenTest almacen;
    private Connection conn;
    private Statement stmt;

    @BeforeEach
    void setUp() throws SQLException {
        
        conn = DriverManager.getConnection("jdbc:sqlite:inventario_test.db");
        stmt = conn.createStatement();

        // Crear tablas necesarias para las pruebas
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

   
        almacen = new AlmacenTest();
    }

    @AfterEach
    void tearDown() throws SQLException {
     
        stmt.execute("DROP TABLE IF EXISTS inventario");
        stmt.execute("DROP TABLE IF EXISTS usuarios");
        stmt.close();
        conn.close();
    }

    @Test
    void testCrearUsuario() {
        almacen.crearUsuario("testuser", "password", "admin");
        String rol = almacen.verificarUsuario("testuser", "password");
        assertEquals("admin", rol);
    }

    @Test
    void testEntradaProducto() {
        almacen.entradaProducto("TestProduct", new ArrayList<>(List.of(5.0, 10.0)), "Planta 1 - Almacén Central");
       
    }

    @Test
    void testSalidaProducto() {
        almacen.entradaProducto("TestProduct", new ArrayList<>(List.of(5.0, 10.0)), "Planta 1 - Almacén Central");
        almacen.salidaProducto("TestProduct", new ArrayList<>(List.of(5.0)), "Planta 1 - Almacén Central");
      
    }

    @Test
    void testVerificarUsuario() {
        almacen.crearUsuario("testuser", "password", "admin");
        String rol = almacen.verificarUsuario("testuser", "password");
        assertEquals("admin", rol);
    }


}
