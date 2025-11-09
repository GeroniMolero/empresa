package com.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dao.EmpleadosDAO;
import com.dao.IEmpleadoDAO;
import com.dao.INominaDAO;
import com.dao.NominasDAO;
import com.model.Empleado;
import com.model.Nomina;

/**
 * Implementación del servicio de nóminas.
 * Patrón Service Layer - encapsula lógica de negocio compleja.
 * Coordina operaciones entre EmpleadosDAO y NominasDAO.
 */
public class NominaService implements INominaService {
    
    private IEmpleadoDAO empleadoDAO;
    private INominaDAO nominaDAO;
    
    /**
     * Constructor con inicialización de DAOs
     */
    public NominaService() {
        this.empleadoDAO = new EmpleadosDAO();
        this.nominaDAO = new NominasDAO();
    }
    
    /**
     * Constructor con inyección explícita para testing
     * @param empleadoDAO DAO de empleados
     * @param nominaDAO DAO de nóminas
     */
    public NominaService(IEmpleadoDAO empleadoDAO, INominaDAO nominaDAO) {
        this.empleadoDAO = empleadoDAO;
        this.nominaDAO = nominaDAO;
    }
    
    @Override
    public Map<String, Object> consultarSalarioEmpleado(String dni) throws Exception {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI proporcionado es nulo o vacío");
        }
        
        // Buscar empleado
        Empleado empleado = empleadoDAO.obtenerEmpleado(dni);
        if (empleado == null) {
            throw new IllegalArgumentException("No se encontró el empleado con DNI " + dni);
        }
        
        // Buscar o calcular salario
        Map<String, Object> registro = nominaDAO.obtenerNomina(dni);
        double salario;
        
        if (registro != null) {
            salario = (double) registro.get("sueldo");
        } else {
            // Si no existe en BD, calcular dinámicamente
            Nomina nomina = new Nomina();
            salario = nomina.sueldo(empleado);
        }
        
        // Preparar resultado
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("empleado", empleado);
        resultado.put("salario", salario);
        
        return resultado;
    }
    
    @Override
    public List<Map<String, Object>> listarTodasLasNominas() throws Exception {
        List<Empleado> empleados = empleadoDAO.listar();
        List<Map<String, Object>> listaNominas = new ArrayList<>();
        
        for (Empleado e : empleados) {
            Map<String, Object> registro = nominaDAO.obtenerNomina(e.getDni());
            
            Map<String, Object> datos = new HashMap<>();
            datos.put("empleado", e);
            
            if (registro != null) {
                datos.put("salario", registro.get("sueldo"));
            } else {
                // Si no existe en BD, calcularlo
                Nomina n = new Nomina();
                datos.put("salario", n.sueldo(e));
            }
            
            listaNominas.add(datos);
        }
        
        return listaNominas;
    }
    
    @Override
    public boolean actualizarSueldo(String dni, double nuevoSueldo) throws SQLException {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        if (nuevoSueldo < 0) {
            throw new IllegalArgumentException("El sueldo no puede ser negativo");
        }
        
        return nominaDAO.actualizarSueldo(dni, nuevoSueldo);
    }
}
