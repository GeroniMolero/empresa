package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.conexion.Conexion;
import com.exceptions.DatosNoCorrectosException;
import com.factory.EmpleadoFactory;
import com.model.Empleado;
import com.model.Nomina;

/**
 * Implementación del DAO para Empleados.
 * Implementa IEmpleadoDAO definiendo las operaciones CRUD concretas.
 * Actúa como Business Delegate manejando la lógica de negocio 
 * y la interacción con la base de datos.
 */
public class EmpleadosDAO implements IEmpleadoDAO {

    // ===========================================================
    // LISTAR TODOS LOS EMPLEADOS
    // ===========================================================
    @Override
    public List<Empleado> listar() throws SQLException, DatosNoCorrectosException {
        String sql = "SELECT * FROM empleados";
        List<Empleado> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(EmpleadoFactory.crearDesdeResultSet(rs));
            }
        }
        return lista;
    }

    // ===========================================================
    // OBTENER UN EMPLEADO POR DNI
    // ===========================================================
    @Override
    public Empleado obtenerEmpleado(String dni) throws SQLException, DatosNoCorrectosException {
        if (dni == null || dni.trim().isEmpty()) {
            throw new SQLException("El DNI proporcionado es nulo o vacío.");
        }

        String sql = "SELECT * FROM empleados WHERE dni=?";
        Empleado empleado = null;

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    empleado = EmpleadoFactory.crearDesdeResultSet(rs);
                }
            }
        }

        if (empleado == null) {
            throw new SQLException("No se encontró ningún empleado con el DNI: " + dni);
        }

        return empleado;
    }

    // ===========================================================
    // ACTUALIZAR UN EMPLEADO (usando HttpServletRequest)
    // ===========================================================
    @Override
    public boolean actualizarEmpleado(HttpServletRequest request)
            throws SQLException, DatosNoCorrectosException {

        Empleado empleado = EmpleadoFactory.crearDesdeRequest(request);

        String sqlEmpleado = "UPDATE empleados SET nombre=?, sexo=?, categoria=?, anyos=? WHERE dni=?";
        String sqlNomina = "UPDATE nominas SET sueldo=? WHERE dni=?";

        try (Connection con = Conexion.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement psEmp = con.prepareStatement(sqlEmpleado)) {
                psEmp.setString(1, empleado.getNombre());
                psEmp.setString(2, empleado.getSexo()); // ✅ ahora es String
                psEmp.setInt(3, empleado.getCategoria());
                psEmp.setInt(4, empleado.getAnyos());
                psEmp.setString(5, empleado.getDni());
                int filasActualizadas = psEmp.executeUpdate();

                if (filasActualizadas == 0) {
                    throw new SQLException("No se encontró ningún empleado con el DNI " + empleado.getDni());
                }
            }

            // Calcular sueldo
            Nomina n = new Nomina();
            double nuevoSueldo = n.sueldo(empleado);

            try (PreparedStatement psNom = con.prepareStatement(sqlNomina)) {
                psNom.setDouble(1, nuevoSueldo);
                psNom.setString(2, empleado.getDni());
                psNom.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException ex) {
            throw new SQLException("Error actualizando empleado: " + ex.getMessage(), ex);
        }
    }

    // ===========================================================
    // BUSCAR EMPLEADOS POR CRITERIO
    // ===========================================================
    @Override
    public List<Empleado> buscarPorCriterio(HttpServletRequest request)
            throws SQLException, DatosNoCorrectosException {

        String campo = request.getParameter("campo");
        String valor = request.getParameter("valor");

        List<String> camposValidos = Arrays.asList("nombre", "dni", "sexo", "categoria", "anyos");
        if (!camposValidos.contains(campo)) {
            throw new SQLException("Campo no válido: " + campo);
        }

        String sql = "SELECT * FROM empleados WHERE " + campo + " LIKE ?";
        List<Empleado> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + valor + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(EmpleadoFactory.crearDesdeResultSet(rs));
            }
        }
        return lista;
    }
}
