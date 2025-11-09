package com.dao;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
 
import com.conexion.Conexion;

/**
 * Implementación del DAO para Nóminas.
 * Implementa INominaDAO definiendo las operaciones concretas.
 * Mejorado con manejo adecuado de recursos y cierre automático.
 */
public class NominasDAO implements INominaDAO {
 
    /**
     * Obtiene la información de nómina de un empleado por su DNI
     * @param dni DNI del empleado
     * @return Map con los datos de la nómina (dni, sueldo) o null si no existe
     * @throws SQLException si hay error al acceder a la base de datos
     */
    @Override
    public Map<String, Object> obtenerNomina(String dni) throws SQLException {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        
        String sql = "SELECT * FROM nominas WHERE dni = ?";
        
        try (Connection connection = Conexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, dni);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Map<String, Object> resultado = new HashMap<>();
                    resultado.put("dni", resultSet.getString("dni"));
                    resultado.put("sueldo", resultSet.getDouble("sueldo"));
                    return resultado;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Actualiza el sueldo de un empleado en su nómina
     * @param dni DNI del empleado
     * @param nuevoSueldo Nuevo sueldo a establecer
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error al actualizar
     */
    @Override
    public boolean actualizarSueldo(String dni, double nuevoSueldo) throws SQLException {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        
        if (nuevoSueldo < 0) {
            throw new IllegalArgumentException("El sueldo no puede ser negativo");
        }
        
        String sql = "UPDATE nominas SET sueldo = ? WHERE dni = ?";
        
        try (Connection connection = Conexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDouble(1, nuevoSueldo);
            statement.setString(2, dni);
            
            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;
        }
    }
}
