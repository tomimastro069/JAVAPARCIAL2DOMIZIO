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
            LibroDAO.insertarLibros(conn);
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


    public static void createTablaPrestamo(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Prestamos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "dni_usuario INT NOT NULL," +
                "id_libro INT NOT NULL," +
                "titulo_libro VARCHAR(100) NOT NULL," +
                "fecha_salida DATE NOT NULL," +
                "fecha_devolucion DATE NOT NULL," +
                "FOREIGN KEY (dni_usuario) REFERENCES Usuario(ID)," +
                "FOREIGN KEY (id_libro) REFERENCES libros(id))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Tabla Prestamos creada correctamente");
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear tabla Prestamos", e);
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
}
 