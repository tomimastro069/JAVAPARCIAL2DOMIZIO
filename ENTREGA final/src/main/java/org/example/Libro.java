package org.example;

public class Libro {
    public String titulo;
    public String clasificacion;
    public int numero;

    public Libro(){
    }

    public Libro(String titulo, String clasificacion, int numero) {
        this.titulo = titulo;
        this.clasificacion = clasificacion;
        this.numero = numero;
    }

    public Libro(int numero, String titulo) {
    }
}
