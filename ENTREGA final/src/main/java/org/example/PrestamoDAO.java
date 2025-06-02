package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    public static void IngresarPrestamo(Connection conn, int dni, int idLibro, String tituloLibro, Date fechaSalida, Date fechaDevolucion) {

        String sql = "INSERT INTO Prestamos (dni_usuario, id_libro, titulo_libro, fecha_salida, fecha_devolucion) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setInt(1, dni);
            pStmt.setInt(2, idLibro);
            pStmt.setString(3, tituloLibro);
            pStmt.setDate(4, new java.sql.Date(fechaSalida.getTime()));
            pStmt.setDate(5, new java.sql.Date(fechaDevolucion.getTime()));
            pStmt.executeUpdate();
            System.out.println("Préstamo registrado correctamente");
        } catch (SQLException e) {

            e.printStackTrace();
            throw new RuntimeException("Error al ingresar préstamo", e);
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
    public static void eliminarPrestamo(Connection conn, int idPrestamo) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar préstamo", e);
        }
    }
}