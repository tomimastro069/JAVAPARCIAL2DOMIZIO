package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

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


}
