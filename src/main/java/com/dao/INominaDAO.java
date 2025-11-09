package com.dao;

import java.sql.SQLException;
import java.util.Map;

/**
 * Interfaz que define el contrato para las operaciones de acceso a datos de Nóminas.
 * Siguiendo el principio de Dependency Inversion (SOLID), los controllers
 * dependerán de esta interfaz en lugar de la implementación concreta.
 */
public interface INominaDAO {
    
    /**
     * Obtiene la información de nómina de un empleado por su DNI
     * @param dni DNI del empleado
     * @return Map con los datos de la nómina (dni, sueldo) o null si no existe
     * @throws SQLException si hay error al acceder a la base de datos
     * @throws IllegalArgumentException si el DNI es nulo o vacío
     */
    Map<String, Object> obtenerNomina(String dni) throws SQLException;
    
    /**
     * Actualiza el sueldo de un empleado en su nómina
     * @param dni DNI del empleado
     * @param nuevoSueldo Nuevo sueldo a establecer
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error al actualizar
     * @throws IllegalArgumentException si el DNI es nulo/vacío o el sueldo es negativo
     */
    boolean actualizarSueldo(String dni, double nuevoSueldo) throws SQLException;
}
