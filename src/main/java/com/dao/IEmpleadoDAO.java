package com.dao;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exceptions.DatosNoCorrectosException;
import com.model.Empleado;

/**
 * Interfaz que define el contrato para las operaciones de acceso a datos de Empleados.
 * Siguiendo el principio de Dependency Inversion (SOLID), los controllers
 * dependerán de esta interfaz en lugar de la implementación concreta.
 */
public interface IEmpleadoDAO {
    
    /**
     * Lista todos los empleados de la base de datos
     * @return Lista de empleados
     * @throws SQLException si hay error al acceder a la base de datos
     * @throws DatosNoCorrectosException si los datos obtenidos no son válidos
     */
    List<Empleado> listar() throws SQLException, DatosNoCorrectosException;
    
    /**
     * Obtiene un empleado específico por su DNI
     * @param dni DNI del empleado a buscar
     * @return Empleado encontrado
     * @throws SQLException si hay error al acceder a la base de datos o no se encuentra el empleado
     * @throws DatosNoCorrectosException si los datos obtenidos no son válidos
     */
    Empleado obtenerEmpleado(String dni) throws SQLException, DatosNoCorrectosException;
    
    /**
     * Actualiza los datos de un empleado existente
     * @param request HttpServletRequest con los datos del empleado a actualizar
     * @return true si la actualización fue exitosa
     * @throws SQLException si hay error al actualizar en la base de datos
     * @throws DatosNoCorrectosException si los datos proporcionados no son válidos
     */
    boolean actualizarEmpleado(HttpServletRequest request) 
            throws SQLException, DatosNoCorrectosException;
    
    /**
     * Busca empleados según un criterio específico
     * @param request HttpServletRequest con los parámetros de búsqueda (campo y valor)
     * @return Lista de empleados que cumplen el criterio
     * @throws SQLException si hay error al buscar en la base de datos
     * @throws DatosNoCorrectosException si los datos obtenidos no son válidos
     */
    List<Empleado> buscarPorCriterio(HttpServletRequest request) 
            throws SQLException, DatosNoCorrectosException;
}
