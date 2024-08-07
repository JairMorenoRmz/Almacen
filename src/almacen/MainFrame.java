package almacen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.sql.Connection;

public class MainFrame extends JFrame {
    private Usuario usuario;
    private Almacen almacen;
    private String rol;

    public MainFrame(Connection conn, Usuario usuario, String rol) {
        this.usuario = usuario;
        this.almacen = new Almacen(conn);
        this.rol = rol;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Sistema de Gestión de Almacén");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("Opciones");
        menuBar.add(menu);

        if (rol.equals("admin")) {
            JMenuItem crearUsuarioMenuItem = new JMenuItem("Crear Usuario");
            menu.add(crearUsuarioMenuItem);
            crearUsuarioMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CrearUsuarioDialog dialog = new CrearUsuarioDialog(MainFrame.this, usuario);
                    dialog.setVisible(true);
                }
            });

            JMenuItem eliminarUsuarioMenuItem = new JMenuItem("Eliminar Usuario");
            menu.add(eliminarUsuarioMenuItem);
            eliminarUsuarioMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = JOptionPane.showInputDialog(MainFrame.this, "Introduce el nombre del usuario a eliminar:");
                    if (username != null) {
                        usuario.eliminarUsuario(username);
                        JOptionPane.showMessageDialog(MainFrame.this, "Usuario eliminado.");
                    }
                }
            });
        }

        if (rol.equals("admin") || rol.equals("planta1") || rol.equals("planta2")) {
            JMenuItem entradaProductoMenuItem = new JMenuItem("Entrada de Producto");
            menu.add(entradaProductoMenuItem);
            entradaProductoMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EntradaProductoDialog dialog = new EntradaProductoDialog(MainFrame.this, almacen);
                    dialog.setVisible(true);
                }
            });

            JMenuItem salidaProductoMenuItem = new JMenuItem("Salida de Producto");
            menu.add(salidaProductoMenuItem);
            salidaProductoMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SalidaProductoDialog dialog = new SalidaProductoDialog(MainFrame.this, almacen);
                    dialog.setVisible(true);
                }
            });
        }

        JMenuItem mostrarInventarioMenuItem = new JMenuItem("Mostrar Inventario");
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

            JMenuItem eliminarHistorialMenuItem = new JMenuItem("Eliminar Materiales del Historial");
            menu.add(eliminarHistorialMenuItem);
            eliminarHistorialMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    almacen.eliminarHistorialAntiguo();
                    JOptionPane.showMessageDialog(MainFrame.this, "Historial antiguo eliminado.");
                }
            });

            JMenuItem eliminarManualMenuItem = new JMenuItem("Eliminar Material Manualmente");
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

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.LIGHT_GRAY);

        JLabel welcomeLabel = new JLabel("Bienvenido al Sistema de Gestión de Almacén", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton entradaButton = new JButton("Entrada de Producto");
        entradaButton.setBackground(Color.GRAY);
        entradaButton.setForeground(Color.WHITE);
        entradaButton.setFont(new Font("Arial", Font.BOLD, 14));
        entradaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntradaProductoDialog dialog = new EntradaProductoDialog(MainFrame.this, almacen);
                dialog.setVisible(true);
            }
        });
        contentPanel.add(entradaButton, gbc);

        gbc.gridy++;
        JButton salidaButton = new JButton("Salida de Producto");
        salidaButton.setBackground(Color.GRAY);
        salidaButton.setForeground(Color.WHITE);
        salidaButton.setFont(new Font("Arial", Font.BOLD, 14));
        salidaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SalidaProductoDialog dialog = new SalidaProductoDialog(MainFrame.this, almacen);
                dialog.setVisible(true);
            }
        });
        contentPanel.add(salidaButton, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
}
