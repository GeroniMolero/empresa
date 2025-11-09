package com.service;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dao.EmpleadosDAO;
import com.dao.IEmpleadoDAO;
import com.exceptions.DatosNoCorrectosException;
import com.model.Empleado;

/**
 * Implementación del servicio de empleados.
 * Patrón Service Layer - encapsula lógica de negocio.
 * Actúa como intermediario entre Controllers y DAOs.
 */
public class EmpleadoService implements IEmpleadoService {
    
    private IEmpleadoDAO empleadoDAO;
    
    /**
     * Constructor con inyección de dependencia del DAO
     */
    public EmpleadoService() {
        this.empleadoDAO = new EmpleadosDAO();
    }
    
    /**
     * Constructor con inyección explícita para testing
     * @param empleadoDAO Implementación del DAO
     */
    public EmpleadoService(IEmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
    }
    
    @Override
    public List<Empleado> listarEmpleados() throws SQLException, DatosNoCorrectosException {
        // Aquí se podría agregar lógica adicional:
        // - Filtrado
        // - Ordenamiento
        // - Cache
        // - Logging de auditoría
        return empleadoDAO.listar();
    }
    
    @Override
    public Empleado buscarEmpleadoPorDni(String dni) throws SQLException, DatosNoCorrectosException {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        return empleadoDAO.obtenerEmpleado(dni);
    }
    
    @Override
    public boolean actualizarEmpleado(HttpServletRequest request) throws SQLException, DatosNoCorrectosException {
        // Aquí se podría agregar:
        // - Validaciones de negocio adicionales
        // - Auditoría de cambios
        // - Notificaciones
        // - Transacciones complejas
        return empleadoDAO.actualizarEmpleado(request);
    }
    
    @Override
    public List<Empleado> buscarEmpleadosPorCriterio(HttpServletRequest request) 
            throws SQLException, DatosNoCorrectosException {
        // Validación de criterios de búsqueda
        String campo = request.getParameter("campo");
        String valor = request.getParameter("valor");
        
        if (campo == null || valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Criterio de búsqueda incompleto");
        }
        
        return empleadoDAO.buscarPorCriterio(request);
    }
}
