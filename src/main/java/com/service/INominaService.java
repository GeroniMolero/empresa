package com.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Interfaz de servicio para lógica de negocio de nóminas.
 * Capa intermedia entre Controllers y DAOs.
 * Patrón Service Layer para encapsular lógica de negocio compleja.
 */
public interface INominaService {
    
    /**
     * Calcula o recupera el salario de un empleado
     * Si existe en BD lo recupera, si no lo calcula dinámicamente
     * @param dni DNI del empleado
     * @return Mapa con empleado y salario
     * @throws Exception
     */
    Map<String, Object> consultarSalarioEmpleado(String dni) throws Exception;
    
    /**
     * Obtiene todas las nóminas del sistema
     * Combina datos de empleados con sus salarios (calculados o almacenados)
     * @return Lista de mapas con empleado y salario
     * @throws Exception
     */
    List<Map<String, Object>> listarTodasLasNominas() throws Exception;
    
    /**
     * Actualiza el salario de un empleado en BD
     * @param dni DNI del empleado
     * @param nuevoSueldo Nuevo salario
     * @return true si se actualizó correctamente
     * @throws SQLException
     */
    boolean actualizarSueldo(String dni, double nuevoSueldo) throws SQLException;
}
