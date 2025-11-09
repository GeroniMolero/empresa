package com.builder;

import com.exceptions.DatosNoCorrectosException;
import com.model.Empleado;

/**
 * Patrón Builder para construcción fluida de objetos Empleado.
 * Permite crear empleados de forma clara y legible, especialmente útil con múltiples parámetros.
 */
public class EmpleadoBuilder {
    
    // Campos obligatorios
    private String nombre;
    private String dni;
    private String sexo;
    
    // Campos opcionales con valores por defecto
    private int categoria = 1;
    private int anyos = 0;
    
    /**
     * Constructor privado - usar método estático builder()
     */
    private EmpleadoBuilder() {
    }
    
    /**
     * Crea una nueva instancia del builder
     * @return Nueva instancia de EmpleadoBuilder
     */
    public static EmpleadoBuilder builder() {
        return new EmpleadoBuilder();
    }
    
    /**
     * Establece el nombre del empleado
     * @param nombre Nombre del empleado
     * @return Este builder para encadenamiento fluido
     */
    public EmpleadoBuilder nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }
    
    /**
     * Establece el DNI del empleado
     * @param dni DNI del empleado
     * @return Este builder para encadenamiento fluido
     */
    public EmpleadoBuilder dni(String dni) {
        this.dni = dni;
        return this;
    }
    
    /**
     * Establece el sexo del empleado
     * @param sexo Sexo del empleado
     * @return Este builder para encadenamiento fluido
     */
    public EmpleadoBuilder sexo(String sexo) {
        this.sexo = sexo;
        return this;
    }
    
    /**
     * Establece la categoría del empleado (1-10)
     * @param categoria Categoría del empleado
     * @return Este builder para encadenamiento fluido
     */
    public EmpleadoBuilder categoria(int categoria) {
        this.categoria = categoria;
        return this;
    }
    
    /**
     * Establece los años de antigüedad del empleado
     * @param anyos Años trabajados
     * @return Este builder para encadenamiento fluido
     */
    public EmpleadoBuilder anyos(int anyos) {
        this.anyos = anyos;
        return this;
    }
    
    /**
     * Construye el objeto Empleado con los datos proporcionados
     * @return Empleado construido
     * @throws DatosNoCorrectosException si los datos no son válidos
     * @throws IllegalStateException si faltan campos obligatorios
     */
    public Empleado build() throws DatosNoCorrectosException {
        // Validar campos obligatorios
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalStateException("El nombre es obligatorio");
        }
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalStateException("El DNI es obligatorio");
        }
        if (sexo == null) {
            throw new IllegalStateException("El sexo es obligatorio");
        }
        
        // Crear y devolver el empleado
        return new Empleado(nombre, dni, sexo, categoria, anyos);
    }
    
    /**
     * Construye un empleado básico (solo con nombre, dni y sexo)
     * @return Empleado con valores por defecto para categoría y años
     */
    public Empleado buildBasico() {
        if (nombre == null || dni == null || sexo == null) {
            throw new IllegalStateException("Nombre, DNI y sexo son obligatorios");
        }
        return new Empleado(nombre, dni, sexo);
    }
}
