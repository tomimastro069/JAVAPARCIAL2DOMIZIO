package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AppBiblioteca extends JFrame {
    public Persona usuario;
    public JFrame ventanaActual;
    public static final String ADMIN_PASSWORD = "1234";
    public static final int MAX_INTENTOS = 3;

    public List<Prestamo> prestamos = new ArrayList<>();
    public Set<Integer> prestamosExtendidos = new HashSet<>();

    public AppBiblioteca() {
        // Inicializar la base de datos al comenzar
        DataBaseManager.inicializarBaseDeDatos();

        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 13));
        mostrarFormularioIngreso();
    }

    public void mostrarCabecera(JFrame frame, String titulo) {
        frame.setTitle(titulo);
        frame.setLayout(new BorderLayout());
        JPanel header = new JPanel();
        header.setBackground(new Color(30, 144, 255));
        header.setPreferredSize(new Dimension(400, 50));
        JLabel lblHeader = new JLabel("📚 Biblioteca Bora");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        header.add(lblHeader);
        frame.add(header, BorderLayout.NORTH);
    }

    public void mostrarFormularioIngreso() {
        ventanaActual = new JFrame();
        mostrarCabecera(ventanaActual, "Biblioteca Bora - Ingreso");

        ventanaActual.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaActual.setSize(400, 300);
        ventanaActual.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField txtNombre = new JTextField(15);
        JTextField txtDni = new JTextField(15);
        JButton btnContinuar = new JButton("Entrar");
        JButton btnAdmin = new JButton("🔒 Ver todos los usuarios (admin)");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDni, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(btnContinuar, gbc);

        gbc.gridy = 3;
        panel.add(btnAdmin, gbc);

        btnContinuar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                int dni = Integer.parseInt(txtDni.getText().trim());
                usuario = new Persona(dni, nombre);

                // Insertar el usuario en la base de datos
                try (Connection conn = DataBaseManager.getConnection()) {
                    DataBaseManager.IngresarUsuario(conn, dni, nombre);
                }

                // Obtener los préstamos directamente desde la base de datos
                try (Connection conn = DataBaseManager.getConnection()) {
                    String query = "SELECT p.id, p.titulo_libro, p.fecha_salida, p.fecha_devolucion " +
                            "FROM prestamos p WHERE p.dni_usuario = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, usuario.dni);
                    ResultSet rs = stmt.executeQuery();

                    prestamos.clear();  // Limpiar cualquier préstamo previo
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String titulo = rs.getString("titulo_libro");
                        Date fechaSalida = rs.getDate("fecha_salida");
                        Date fechaDevolucion = rs.getDate("fecha_devolucion");

                        Prestamo prestamo = new Prestamo(id, fechaSalida, fechaDevolucion, new Libro(id, titulo), usuario);
                        prestamos.add(prestamo);
                    }
                }

                ventanaActual.dispose();
                mostrarMenuPrincipal();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventanaActual, "Datos inválidos.");
            }
        });

        btnAdmin.addActionListener(e -> accederComoAdmin());

        ventanaActual.add(panel, BorderLayout.CENTER);
        ventanaActual.setVisible(true);
    }

    public void mostrarMenuPrincipal() {
        ventanaActual = new JFrame();
        mostrarCabecera(ventanaActual, "Menú - Biblioteca Bora");

        ventanaActual.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaActual.setSize(400, 360);
        ventanaActual.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton verPrestamos = new JButton("Ver mis préstamos");
        JButton nuevoPrestamo = new JButton("Hacer un nuevo préstamo");
        JButton extenderPrestamo = new JButton("Extender un préstamo");
        JButton devolverLibro = new JButton("Devolver un libro");
        JButton salir = new JButton("Salir");

        verPrestamos.addActionListener(e -> verMisPrestamos());
        nuevoPrestamo.addActionListener(e -> hacerNuevoPrestamo());
        extenderPrestamo.addActionListener(e -> extenderPrestamo());
        devolverLibro.addActionListener(e -> devolverLibro());
        salir.addActionListener(e -> System.exit(0));

        panel.add(verPrestamos);
        panel.add(nuevoPrestamo);
        panel.add(extenderPrestamo);
        panel.add(devolverLibro);
        panel.add(salir);

        ventanaActual.add(panel, BorderLayout.CENTER);
        ventanaActual.setVisible(true);
    }

    public void hacerNuevoPrestamo() {
        try (Connection conn = DataBaseManager.getConnection()) {
            // Obtener libros disponibles
            PreparedStatement stmt = conn.prepareStatement("SELECT id, titulo, categoria FROM libros");
            ResultSet rs = stmt.executeQuery();

            List<Libro> librosDisponibles = new ArrayList<>();
            Map<String, Libro> tituloALibro = new HashMap<>();

            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getString("titulo"),
                        rs.getString("categoria"),
                        rs.getInt("id")
                );
                librosDisponibles.add(libro);
                tituloALibro.put(libro.titulo, libro);
            }

            if (librosDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay libros disponibles.");
                return;
            }


            String[] titulos = new String[librosDisponibles.size()];
            for (int i = 0; i < librosDisponibles.size(); i++) {
                titulos[i] = librosDisponibles.get(i).titulo;
            }

            String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Seleccione un libro:",
                    "Nuevo Préstamo",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    titulos,
                    titulos[0]
            );

            if (seleccion != null) {
                Libro libroSeleccionado = tituloALibro.get(seleccion);

                if (libroSeleccionado == null) {
                    JOptionPane.showMessageDialog(null, "Libro no encontrado.");
                    return;
                }

                Integer[] dias = {1, 3, 5, 7, 10, 14};
                Integer seleccionDias = (Integer) JOptionPane.showInputDialog(
                        null,
                        "¿Cuántos días querés el libro?",
                        "Duración",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        dias,
                        dias[3]
                );

                if (seleccionDias != null) {
                    Date salida = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(salida);
                    cal.add(Calendar.DAY_OF_MONTH, seleccionDias);
                    Date devolucion = cal.getTime();

                    java.sql.Date sqlSalida = new java.sql.Date(salida.getTime());
                    java.sql.Date sqlDevolucion = new java.sql.Date(devolucion.getTime());


                    int nuevoId = prestamos.isEmpty() ? 1 : prestamos.get(prestamos.size() - 1).numero + 1;
                    Prestamo nuevo = new Prestamo(
                            nuevoId,
                            salida,
                            devolucion,
                            libroSeleccionado,
                            usuario
                    );
                    prestamos.add(nuevo);

                    DataBaseManager.IngresarPrestamo(
                            conn,
                            usuario.dni,
                            libroSeleccionado.numero,
                            libroSeleccionado.titulo,
                            sqlSalida,
                            sqlDevolucion
                    );

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    JOptionPane.showMessageDialog(
                            null,
                            "Préstamo creado:\n" +
                                    "Libro: " + libroSeleccionado.titulo + "\n" +
                                    "Desde: " + sdf.format(salida) + "\n" +
                                    "Hasta: " + sdf.format(devolucion)
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al crear el préstamo: " + e.getMessage());
        }
    }
    public void verMisPrestamos() {
        if (prestamos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tenés préstamos registrados.");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        for (Prestamo p : prestamos) {
            sb.append("📗 ").append(p.prestado.titulo)
                    .append(" | Del ").append(sdf.format(p.dia_prestano))
                    .append(" al ").append(sdf.format(p.devolucion)).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Tus préstamos", JOptionPane.INFORMATION_MESSAGE);
    }

    public void extenderPrestamo() {
        if (prestamos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay préstamos para extender.");
            return;
        }

        String[] opciones = new String[prestamos.size()];
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo p = prestamos.get(i);
            opciones[i] = p.numero + ": " + p.prestado.titulo;
        }

        String seleccion = (String) JOptionPane.showInputDialog(null,
                "Seleccione el préstamo a extender:", "Extensión",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion != null) {
            int numero = Integer.parseInt(seleccion.split(":")[0]);
            if (prestamosExtendidos.contains(numero)) {
                JOptionPane.showMessageDialog(null, "Este préstamo ya fue extendido una vez.");
                return;
            }

            Prestamo p = prestamos.get(numero - 1);
            Integer[] diasExtra = {1, 3, 5, 7};
            Integer extra = (Integer) JOptionPane.showInputDialog(null,
                    "¿Cuántos días querés agregar?", "Extensión",
                    JOptionPane.QUESTION_MESSAGE, null, diasExtra, diasExtra[0]);

            if (extra != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(p.devolucion);
                cal.add(Calendar.DAY_OF_MONTH, extra);
                Prestamo nuevo = new Prestamo(p.numero, p.dia_prestano, cal.getTime(), p.prestado, p.socio);
                prestamos.set(numero - 1, nuevo);
                prestamosExtendidos.add(numero);
                JOptionPane.showMessageDialog(null, "Préstamo extendido.");
            }
        }
    }

    public void devolverLibro() {
        if (prestamos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay préstamos activos.");
            return;
        }

        String[] opciones = new String[prestamos.size()];
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo p = prestamos.get(i);
            opciones[i] = p.numero + ": " + p.prestado.titulo;
        }

        String seleccion = (String) JOptionPane.showInputDialog(null,
                "¿Qué libro vas a devolver?", "Devolución",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion != null) {
            int numero = Integer.parseInt(seleccion.split(":")[0]);
            prestamos.removeIf(p -> p.numero == numero);
            prestamosExtendidos.remove(numero);
            JOptionPane.showMessageDialog(null, "Libro devuelto correctamente.");
        }
    }

    public void accederComoAdmin() {
        int intentos = 0;
        while (intentos < MAX_INTENTOS) {
            String input = JOptionPane.showInputDialog("Ingrese la contraseña de administrador:");

            if (input == null) {
                JOptionPane.showMessageDialog(null, "Acción cancelada.");
                return;
            }

            if (ADMIN_PASSWORD.equals(input)) {
                mostrarUsuariosRegistrados();
                return;
            } else {
                intentos++;
                JOptionPane.showMessageDialog(null, "Contraseña incorrecta. Intento " + intentos + " de " + MAX_INTENTOS);
            }
        }
        JOptionPane.showMessageDialog(null, "Demasiados intentos. Cerrando programa.");
        System.exit(0);
    }

    public void mostrarUsuariosRegistrados() {
        try (Connection conn = DataBaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.Nombre, p.titulo_libro, p.fecha_salida, p.fecha_devolucion " +
                            "FROM prestamos p " +
                            "JOIN usuario u ON p.dni_usuario = u.ID"
            );
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "No hay préstamos registrados.");
                return;
            }

            Map<String, List<String>> prestamosPorUsuario = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                String nombre = rs.getString("Nombre");
                String titulo = rs.getString("titulo_libro");
                Date salida = rs.getDate("fecha_salida");
                Date devolucion = rs.getDate("fecha_devolucion");

                String detalle = "   📘 " + titulo + " (" + sdf.format(salida) + " → " + sdf.format(devolucion) + ")";
                if (!prestamosPorUsuario.containsKey(nombre)) {
                    prestamosPorUsuario.put(nombre, new ArrayList<>());
                }
                prestamosPorUsuario.get(nombre).add(detalle);
            }

            StringBuilder sb = new StringBuilder();
            for (String nombre : prestamosPorUsuario.keySet()) {
                sb.append("👤 ").append(nombre).append(":\n");
                for (String detalle : prestamosPorUsuario.get(nombre)) {
                    sb.append(detalle).append("\n");
                }
                sb.append("\n");
            }

            JTextArea area = new JTextArea(sb.toString(), 20, 40);
            area.setEditable(false);
            JScrollPane scroll = new JScrollPane(area);
            JOptionPane.showMessageDialog(null, scroll, "Usuarios y préstamos", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar los usuarios.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppBiblioteca::new);
    }
}
