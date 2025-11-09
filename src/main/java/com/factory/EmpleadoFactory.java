package com.factory;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.exceptions.DatosNoCorrectosException;
import com.model.Empleado;

/**
 * Patrón Factory para la creación centralizada de objetos Empleado.
 * Simplifica la construcción desde diferentes fuentes de datos.
 */
public class EmpleadoFactory {
    
    /**
     * Crea un Empleado a partir de un ResultSet de la base de datos
     * @param rs ResultSet posicionado en el registro del empleado
     * @return Empleado construido
     * @throws SQLException si hay error al leer los datos
     * @throws DatosNoCorrectosException si los datos no son válidos
     */
    public static Empleado crearDesdeResultSet(ResultSet rs) 
            throws SQLException, DatosNoCorrectosException {
        
        String sexoStr = rs.getString("sexo");
        if (sexoStr == null) {
            sexoStr = "";
        }
        
        return new Empleado(
            rs.getString("nombre"),
            rs.getString("dni"),
            sexoStr.trim(),
            rs.getInt("categoria"),
            rs.getInt("anyos")
        );
    }
    
    /**
     * Crea un Empleado a partir de un HttpServletRequest
     * @param request Request HTTP con los parámetros del empleado
     * @return Empleado construido
     * @throws DatosNoCorrectosException si los datos no son válidos
     * @throws NumberFormatException si los números no son válidos
     */
    public static Empleado crearDesdeRequest(HttpServletRequest request) 
            throws DatosNoCorrectosException, NumberFormatException {
        
        String nombre = request.getParameter("nombre");
        String dni = request.getParameter("dni");
        String sexo = request.getParameter("sexo");
        int categoria = Integer.parseInt(request.getParameter("categoria"));
        int anyos = Integer.parseInt(request.getParameter("anyos"));
        
        return new Empleado(nombre, dni, sexo, categoria, anyos);
    }
    
    /**
     * Crea un Empleado con valores por defecto (categoria 1, 0 años)
     * @param nombre Nombre del empleado
     * @param dni DNI del empleado
     * @param sexo Sexo del empleado
     * @return Empleado construido con valores por defecto
     */
    public static Empleado crearEmpleadoBasico(String nombre, String dni, String sexo) {
        return new Empleado(nombre, dni, sexo);
    }
    
    /**
     * Crea un Empleado vacío (para inicialización)
     * @return Empleado vacío
     */
    public static Empleado crearEmpleadoVacio() {
        return new Empleado();
    }
}
