package com.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.service.INominaService;
import com.service.NominaService;

/**
 * Controlador para la gestión de nóminas.
 * Ahora es invocado a través del FrontController en lugar de directamente.
 * Usa interfaces IEmpleadoDAO e INominaDAO en lugar de implementaciones concretas,
 * siguiendo el principio de Dependency Inversion (SOLID).
 * Anteriormente mapeado en: @WebServlet("/NominasController")
 * Puede consultar el salario de un empleado o listar todas las nóminas existentes.
 */
public class NominasController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private INominaService nominaService;

    @Override
    public void init() throws ServletException {
        nominaService = new NominaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "formularioSalario";

        try {
            switch (action) {
                case "formularioSalario":
                    // Mostrar el formulario para introducir DNI
                    request.getRequestDispatcher("/salarioForm.jsp").forward(request, response);
                    break;
                case "listarNominas":
                    listarNominas(request, response);
                    break;
                default:
                    request.getRequestDispatcher("/salarioForm.jsp").forward(request, response);
                    break;
            }
        } catch (Exception e) {
            // Log sencillo. En producción usar un logger.
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
        }
    }

    /**
     * Consulta y muestra el salario de un empleado concreto.
     * Primero intenta obtenerlo desde la base de datos.
     * Si no existe, lo calcula con la clase Nomina.
     */
    private void consultarSalario(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String dni = request.getParameter("dni");
        Map<String, Object> datos = nominaService.consultarSalarioEmpleado(dni);
        request.setAttribute("empleado", datos.get("empleado"));
        request.setAttribute("salario", datos.get("salario"));
        request.getRequestDispatcher("/salarioResultado.jsp").forward(request, response);
    }

    /**
     * Lista todas las nóminas registradas en la base de datos.
     */
    private void listarNominas(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Map<String, Object>> listaNominas = nominaService.listarTodasLasNominas();
        request.setAttribute("listaNominas", listaNominas);
        request.getRequestDispatcher("/nominas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if ("consultarSalario".equals(action)) {
                consultarSalario(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción POST no reconocida: " + action);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
        }
    }
}
