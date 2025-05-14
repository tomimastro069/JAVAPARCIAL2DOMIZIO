package org.example;

import java.util.ArrayList;
import java.util.Date;

public class Prestamo {
    public int numero;
    public Date dia_prestano;
    public  Date devolucion;
    public Libro prestado;
    public Persona socio;
    public ArrayList<Persona> ArrayPersona = new ArrayList<>();
    public Prestamo(){
    }

    public Prestamo(int numero, Date dia_prestano, Date devolucion, Libro prestado, Persona socio) {
        this.numero = numero;
        this.dia_prestano = dia_prestano;
        this.devolucion = devolucion;
        this.prestado = prestado;
        this.socio = socio;
    }
}
