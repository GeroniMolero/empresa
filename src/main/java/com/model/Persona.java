package com.model;

public class Persona {

    private String nombre;
    private String dni;
    private String sexo;

    // ===================== CONSTRUCTORES =====================
    public Persona(String nombre, String dni, String sexo) {
        this.nombre = nombre;
        this.dni = dni;
        this.sexo = sexo;
    }

    public Persona() {
    }

    public Persona(String nombre, String sexo) {
        this.nombre = nombre;
        this.sexo = sexo;
    }

    // ===================== GETTERS & SETTERS =====================
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    // ===================== OTROS MÉTODOS =====================
    public void imprime(String dni, String nombre) {
        System.out.println("Nombre: " + nombre + ", Dni: " + dni);
    }
}
