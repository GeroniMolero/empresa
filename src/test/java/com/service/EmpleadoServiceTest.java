package com.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.dao.IEmpleadoDAO;
import com.exceptions.DatosNoCorrectosException;
import com.model.Empleado;

/**
 * Tests unitarios para EmpleadoService usando Mockito.
 * Demuestra testing con mocks sin necesidad de base de datos real.
 */
public class EmpleadoServiceTest {
    
    private EmpleadoService service;
    private IEmpleadoDAO mockDAO;
    
    @Before
    public void setUp() {
        // Crear mock del DAO
        mockDAO = mock(IEmpleadoDAO.class);
        // Inyectar mock en el servicio
        service = new EmpleadoService(mockDAO);
    }
    
    @Test
    public void testListarEmpleados() throws SQLException, DatosNoCorrectosException {
        // Preparar datos de prueba
        Empleado emp1 = new Empleado("Juan", "12345678A", "M", 5, 10);
        Empleado emp2 = new Empleado("María", "87654321B", "F", 3, 5);
        List<Empleado> empleadosEsperados = Arrays.asList(emp1, emp2);
        
        // Configurar comportamiento del mock
        when(mockDAO.listar()).thenReturn(empleadosEsperados);
        
        // Ejecutar método a testear
        List<Empleado> resultado = service.listarEmpleados();
        
        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("María", resultado.get(1).getNombre());
        
        // Verificar que se llamó al DAO
        verify(mockDAO, times(1)).listar();
    }
    
    @Test
    public void testBuscarEmpleadoPorDni() throws SQLException, DatosNoCorrectosException {
        // Preparar datos
        Empleado empleadoEsperado = new Empleado("Juan", "12345678A", "M", 5, 10);
        when(mockDAO.obtenerEmpleado("12345678A")).thenReturn(empleadoEsperado);
        
        // Ejecutar
        Empleado resultado = service.buscarEmpleadoPorDni("12345678A");
        
        // Verificar
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("12345678A", resultado.getDni());
        verify(mockDAO).obtenerEmpleado("12345678A");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBuscarEmpleadoPorDniVacio() throws SQLException, DatosNoCorrectosException {
        // Ejecutar con DNI vacío debe lanzar excepción
        service.buscarEmpleadoPorDni("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBuscarEmpleadoPorDniNulo() throws SQLException, DatosNoCorrectosException {
        // Ejecutar con DNI nulo debe lanzar excepción
        service.buscarEmpleadoPorDni(null);
    }
}
