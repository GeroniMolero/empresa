package com.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.ErrorHandler;

/**
 * Patrón Front Controller - Punto único de entrada para todas las peticiones.
 * Centraliza el enrutamiento hacia los controladores específicos (EmpleadosController o NominasController).
 * 
 * Los controladores internos usan interfaces (IEmpleadoDAO, INominaDAO) siguiendo
 * el principio de Dependency Inversion, facilitando testing y mantenimiento.
 * 
 * Rutas soportadas:
 * - /app/empleados?action=... -> EmpleadosController
 * - /app/nominas?action=... -> NominasController
 */
@WebServlet("/app/*")
public class FrontController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private EmpleadosController empleadosController;
    private NominasController nominasController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Inicializar los controladores específicos
        empleadosController = new EmpleadosController();
        empleadosController.init();
        
        nominasController = new NominasController();
        nominasController.init();
        
        log("FrontController inicializado correctamente");
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        // Logging para debugging
        log("Petición recibida: " + request.getMethod() + " " + pathInfo);
        
        try {
            // Validar que existe pathInfo
            if (pathInfo == null || pathInfo.equals("/")) {
                redirigirAHome(request, response);
                return;
            }
            
            // Enrutar según el path
            if (pathInfo.startsWith("/empleados")) {
                log("Redirigiendo a EmpleadosController");
                empleadosController.service(request, response);
                
            } else if (pathInfo.startsWith("/nominas")) {
                log("Redirigiendo a NominasController");
                nominasController.service(request, response);
                
            } else {
                // Ruta no reconocida
                response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                    "Recurso no encontrado: " + pathInfo);
            }
            
        } catch (Exception e) {
            log("Error en FrontController: " + e.getMessage(), e);
            ErrorHandler.handleError(e, request, response, getServletContext());
        }
    }
    
    /**
     * Redirige a la página de inicio cuando no se especifica un recurso
     */
    private void redirigirAHome(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
    
    @Override
    public void destroy() {
        log("FrontController destruido");
        super.destroy();
    }
}
