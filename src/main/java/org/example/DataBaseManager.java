package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataBaseManager {
    static Scanner scanner = new Scanner(System.in);
    private static final String Db_URL = "jdbc:mysql://localhost:3306/bd_parcialjava2_domizio";
    private static final String user = "root";
    private static final String password = "1234";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(Db_URL, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    public static void inicializarBaseDeDatos() {
        try (Connection conn = getConnection()) {
            createTableUsuario(conn);
            createTablaLibros(conn);
            createTablaPrestamo(conn);
            insertarLibros(conn);
            System.out.println("Base de datos inicializada correctamente");
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }

    public static void createTableUsuario(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Usuario (" +
                "ID INT PRIMARY KEY, " +
                "Nombre VARCHAR(52) NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Tabla Usuario creada/verificada");
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear tabla Usuario", e);
        }
    }

    public static void IngresarUsuario(Connection conn, int dni, String nombre) {
        String sql = "INSERT INTO Usuario (ID, Nombre) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE Nombre = VALUES(Nombre)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dni);
            stmt.setString(2, nombre);
            stmt.executeUpdate();
            System.out.println("Usuario ingresado/actualizado: " + nombre + " (DNI: " + dni + ")");
        } catch (SQLException e) {
            throw new RuntimeException("Error al ingresar usuario", e);
        }
    }

    public static void createTablaPrestamo(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Prestamos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "dni_usuario INT NOT NULL," +
                "id INT NOT NULL," +
                "titulo_libro VARCHAR(100) NOT NULL," +
                "fecha_salida DATE NOT NULL," +
                "fecha_devolucion DATE NOT NULL," +
                "FOREIGN KEY (dni_usuario) REFERENCES Usuario(ID)," +
                "FOREIGN KEY (id) REFERENCES libros(id))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Tabla Prestamos creada correctamente");
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear tabla Prestamos", e);
        }
    }

    public static void IngresarPrestamo(Connection conn, int dni, int id, String tituloLibro,
                                        Date fechaSalida, Date fechaDevolucion) {
        String sql = "INSERT INTO Prestamos (dni_usuario, id, titulo_libro, fecha_salida, fecha_devolucion) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setInt(1, dni);
            pStmt.setInt(2, id);
            pStmt.setString(3, tituloLibro);
            pStmt.setDate(4, new java.sql.Date(fechaSalida.getTime()));
            pStmt.setDate(5, new java.sql.Date(fechaDevolucion.getTime()));
            pStmt.executeUpdate();
            System.out.println("Préstamo registrado correctamente");
        } catch (SQLException e) {
            throw new RuntimeException("Error al ingresar préstamo", e);
        }
    }

    private static String obtenerTituloLibro(Connection conn, int idLibro) {
        String sql = "SELECT titulo FROM libros WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLibro);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("titulo");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener título del libro", e);
        }
    }

    public static void createTablaLibros(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS libros (" +
                "id INT PRIMARY KEY," +
                "titulo VARCHAR(100) NOT NULL," +
                "categoria VARCHAR(50) NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Tabla libros creada/verificada");
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear tabla libros", e);
        }
    }

    public static void insertarLibros(Connection conn) {
        String sql = "INSERT IGNORE INTO libros (id, titulo, categoria) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 101);
            stmt.setString(2, "Java Básico");
            stmt.setString(3, "Programación");
            stmt.addBatch();

            stmt.setInt(1, 102);
            stmt.setString(2, "Estructuras de Datos");
            stmt.setString(3, "Informática");
            stmt.addBatch();

            stmt.setInt(1, 103);
            stmt.setString(2, "Bases de Datos");
            stmt.setString(3, "Tecnología");
            stmt.addBatch();

            stmt.setInt(1, 104);
            stmt.setString(2, "Sistemas Operativos");
            stmt.setString(3, "Informática");
            stmt.addBatch();

            stmt.setInt(1, 105);
            stmt.setString(2, "Redes de Computadoras");
            stmt.setString(3, "Infraestructura");
            stmt.addBatch();


            stmt.setInt(1, 106);
            stmt.setString(2, "Inteligencia Artificial");
            stmt.setString(3, "IA");
            stmt.addBatch();

            stmt.setInt(1, 107);
            stmt.setString(2, "Mecanica 5");
            stmt.setString(3, "Tutorial");
            stmt.addBatch();

            stmt.executeBatch();
            System.out.println("Libros insertados/verificados correctamente.");
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar libros", e);
        }
    }

    public static List<Libro> obtenerLibrosDisponibles(Connection conn) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT id, titulo FROM libros ORDER BY titulo";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                libros.add(new Libro(rs.getInt("id"), rs.getString("titulo")));
            }
            return libros;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener libros disponibles", e);
        }
    }

    public static List<Prestamo> obtenerPrestamosUsuario(Connection conn, int dniUsuario) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.id, p.id_libro, l.titulo, p.fecha_salida, p.fecha_devolucion " +
                "FROM Prestamos p " +
                "JOIN libros l ON p.id_libro = l.id " +
                "WHERE p.dni_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dniUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                prestamos.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getDate("fecha_salida"),
                        rs.getDate("fecha_devolucion"),
                        new Libro(rs.getInt("id_libro"), rs.getString("titulo")),
                        null
                ));
            }
            return prestamos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener préstamos del usuario", e);
        }
    }

    public static void main(String[] args) {
        inicializarBaseDeDatos();
    }
}