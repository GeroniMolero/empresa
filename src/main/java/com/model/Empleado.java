package com.model;

import com.exceptions.DatosNoCorrectosException;

public class Empleado extends Persona {

    private int categoria;
    private int anyos;

    // ===================== CONSTRUCTORES =====================
    public Empleado(String nombre, String dni, String sexo, int categoria, int anyos)
            throws DatosNoCorrectosException {
        super(nombre, dni, sexo);

        if (categoria >= 1 && categoria <= 10 && anyos >= 0) {
            this.categoria = categoria;
            this.anyos = anyos;
        } else {
            throw new DatosNoCorrectosException("Datos no correctos");
        }
    }

    public Empleado(String nombre, String dni, String sexo) {
        super(nombre, dni, sexo);
        this.categoria = 1;
        this.anyos = 0;
    }

    public Empleado() {
        super();
    }

    // ===================== GETTERS & SETTERS =====================
    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getAnyos() {
        return anyos;
    }

    public void setAnyos(int anyos) {
        this.anyos = anyos;
    }

    // ===================== MÉTODOS DE UTILIDAD =====================
    public void incrAnyo() {
        this.anyos++;
    }

    public void imprime() {
        System.out.println("Nombre: " + getNombre() + ", Dni: " + getDni() + ", Sexo: " + getSexo()
                + ", Categoria: " + categoria + ", Años trabajados: " + anyos);
    }
}
