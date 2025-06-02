package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersonaDAO {

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
}
