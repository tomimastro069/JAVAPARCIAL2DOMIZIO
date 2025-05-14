package org.example;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DataBaseManager.getConnection();

            // Crear tablas
            DataBaseManager.createTablaLibros(conn);
            DataBaseManager.insertarLibros(conn);
            DataBaseManager.createTableUsuario(conn);
            DataBaseManager.createTablaPrestamo(conn);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
        }

        // Lanzar la interfaz gráfica
        SwingUtilities.invokeLater(AppBiblioteca::new);
    }
}
