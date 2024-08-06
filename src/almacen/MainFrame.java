package almacen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.awt.*;

public class MainFrame extends JFrame {
    private Usuario usuario;
    private Almacen almacen;
    private String rol;

    public MainFrame(Connection conn, Usuario usuario, String rol) {
        this.usuario = usuario;
        this.almacen = new Almacen(conn);
        this.rol = rol;

        setTitle("Almacén");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("Opciones");
        menuBar.add(menu);

        if (rol.equals("admin")) {
            JMenuItem crearUsuarioMenuItem = new JMenuItem("Crear usuario");
            menu.add(crearUsuarioMenuItem);
            crearUsuarioMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CrearUsuarioDialog dialog = new CrearUsuarioDialog(MainFrame.this, usuario);
                    dialog.setVisible(true);
                }
            });
        }

        if (rol.equals("admin") || rol.equals("planta1") || rol.equals("planta2")) {
            JMenuItem entradaProductoMenuItem = new JMenuItem("Entrada de producto");
            menu.add(entradaProductoMenuItem);
            entradaProductoMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EntradaProductoDialog dialog = new EntradaProductoDialog(MainFrame.this, almacen);
                    dialog.setVisible(true);
                }
            });

            JMenuItem salidaProductoMenuItem = new JMenuItem("Salida de producto");
            menu.add(salidaProductoMenuItem);
            salidaProductoMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SalidaProductoDialog dialog = new SalidaProductoDialog(MainFrame.this, almacen);
                    dialog.setVisible(true);
                }
            });
        }

        JMenuItem mostrarInventarioMenuItem = new JMenuItem("Mostrar inventario");
        menu.add(mostrarInventarioMenuItem);
        mostrarInventarioMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MostrarInventarioDialog dialog = new MostrarInventarioDialog(MainFrame.this, almacen);
                dialog.setVisible(true);
            }
        });

        if (rol.equals("admin")) {
            JMenuItem historialMenuItem = new JMenuItem("Historial de Entradas y Salidas");
            menu.add(historialMenuItem);
            historialMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MostrarHistorialDialog dialog = new MostrarHistorialDialog(MainFrame.this, almacen);
                    dialog.setVisible(true);
                }
            });

            JMenuItem eliminarHistorialMenuItem = new JMenuItem("Eliminar materiales del historial");
            menu.add(eliminarHistorialMenuItem);
            eliminarHistorialMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    almacen.eliminarHistorialAntiguo();
                    JOptionPane.showMessageDialog(MainFrame.this, "Historial antiguo eliminado.");
                }
            });

            JMenuItem eliminarManualMenuItem = new JMenuItem("Eliminar manualmente un material del historial");
            menu.add(eliminarManualMenuItem);
            eliminarManualMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String idStr = JOptionPane.showInputDialog(MainFrame.this, "Introduce el ID del material a eliminar:");
                    int id = Integer.parseInt(idStr);
                    almacen.eliminarHistorialManual(id);
                    JOptionPane.showMessageDialog(MainFrame.this, "Material eliminado.");
                }
            });
        }
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Bienvenido al Sistema de Gestión de Almacén", JLabel.CENTER);
        mainPanel.add(welcomeLabel, BorderLayout.CENTER);
        add(mainPanel);
    }
}

