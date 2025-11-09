package com.service;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exceptions.DatosNoCorrectosException;
import com.model.Empleado;

/**
 * Interfaz de servicio para lógica de negocio de empleados.
 * Capa intermedia entre Controllers y DAOs.
 * Patrón Service Layer para encapsular lógica de negocio compleja.
 */
public interface IEmpleadoService {
    
    /**
     * Obtiene todos los empleados del sistema
     * @return Lista de empleados
     * @throws SQLException
     * @throws DatosNoCorrectosException
     */
    List<Empleado> listarEmpleados() throws SQLException, DatosNoCorrectosException;
    
    /**
     * Busca un empleado por su DNI
     * @param dni DNI del empleado
     * @return Empleado encontrado o null
     * @throws SQLException
     * @throws DatosNoCorrectosException
     */
    Empleado buscarEmpleadoPorDni(String dni) throws SQLException, DatosNoCorrectosException;
    
    /**
     * Actualiza la información de un empleado
     * @param request Petición con datos del formulario
     * @return true si se actualizó correctamente
     * @throws SQLException
     * @throws DatosNoCorrectosException
     */
    boolean actualizarEmpleado(HttpServletRequest request) throws SQLException, DatosNoCorrectosException;
    
    /**
     * Busca empleados según criterio especificado
     * @param request Petición con campo y valor de búsqueda
     * @return Lista de empleados que cumplen el criterio
     * @throws SQLException
     * @throws DatosNoCorrectosException
     */
    List<Empleado> buscarEmpleadosPorCriterio(HttpServletRequest request) throws SQLException, DatosNoCorrectosException;
}
