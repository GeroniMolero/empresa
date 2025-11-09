package com.util;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Clase utilitaria para manejo centralizado de errores.
 * Proporciona métodos estáticos para sanitizar mensajes de error
 * y manejar excepciones de forma consistente en toda la aplicación.
 */
public class ErrorHandler {

    /**
     * Sanitiza mensajes de error para evitar exponer información sensible.
     * Permite mensajes de negocio pero oculta detalles técnicos (SQL, rutas, etc.)
     * 
     * @param message El mensaje original de la excepción
     * @return Mensaje sanitizado seguro para mostrar al usuario
     */
    public static String sanitizeErrorMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Se ha producido un error inesperado.";
        }
        
        // Lista blanca: mensajes de negocio permitidos
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("dni") || 
            lowerMessage.contains("empleado") || 
            lowerMessage.contains("nómina") ||
            lowerMessage.contains("nomina") ||
            lowerMessage.contains("categoría") ||
            lowerMessage.contains("categoria") ||
            lowerMessage.contains("salario") ||
            lowerMessage.contains("años") ||
            lowerMessage.contains("anos")) {
            return message;
        }
        
        // Lista negra: ocultar mensajes técnicos
        if (lowerMessage.contains("sql") || 
            lowerMessage.contains("connection") || 
            lowerMessage.contains("database") ||
            lowerMessage.contains("table") || 
            lowerMessage.contains("column") ||
            lowerMessage.contains("jdbc") ||
            lowerMessage.contains("exception") ||
            lowerMessage.contains("null pointer") ||
            lowerMessage.contains("nullpointer") ||
            lowerMessage.contains("class") ||
            lowerMessage.contains("stack")) {
            return "Error al procesar la solicitud. Por favor, contacte al administrador del sistema.";
        }
        
        // Mensaje genérico para otros casos
        return "Se ha producido un error. Por favor, inténtelo de nuevo.";
    }

    /**
     * Maneja una excepción y redirige a la página de error con sanitización.
     * En modo development, añade información técnica adicional.
     * 
     * @param e La excepción a manejar
     * @param request La petición HTTP
     * @param response La respuesta HTTP
     * @param servletContext Contexto del servlet para leer configuración de entorno
     * @throws ServletException Si hay error al hacer forward
     * @throws IOException Si hay error de I/O
     */
    public static void handleError(Exception e, HttpServletRequest request, 
                                   HttpServletResponse response, ServletContext servletContext) 
            throws ServletException, IOException {
        
        String environment = servletContext.getInitParameter("app.environment");
        boolean isDevelopment = "development".equals(environment);
        
        // Sanitizar mensaje para usuario final
        String userMessage = sanitizeErrorMessage(e.getMessage());
        request.setAttribute("error", userMessage);
        
        // Solo en desarrollo: exponer detalles técnicos
        if (isDevelopment) {
            request.setAttribute("errorType", e.getClass().getSimpleName());
            request.setAttribute("errorStackTrace", getStackTraceAsString(e));
        }
        
        // Forward a página de error
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }

    /**
     * Convierte el stack trace de una excepción en String.
     * Útil para logging o mostrar en modo desarrollo.
     * 
     * @param e La excepción
     * @return Stack trace como String
     */
    public static String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Versión simplificada de handleError para controladores que no usan
     * diferenciación entre development/production (siempre sanitizan).
     * Loguea el stack trace completo en el servidor para debugging.
     * 
     * @param e La excepción a manejar
     * @param request La petición HTTP
     * @param response La respuesta HTTP
     * @throws ServletException Si hay error al hacer forward
     * @throws IOException Si hay error de I/O
     */
    public static void handleErrorSimple(Exception e, HttpServletRequest request, 
                                        HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Loguear error completo usando ServletContext logger (aparece en localhost.log)
        request.getServletContext().log("=== ERROR EN APLICACIÓN ===");
        request.getServletContext().log("Timestamp: " + new java.util.Date());
        request.getServletContext().log("URI: " + request.getRequestURI());
        request.getServletContext().log("Método: " + request.getMethod());
        request.getServletContext().log("Mensaje original: " + e.getMessage());
        request.getServletContext().log("Stack trace:", e);
        request.getServletContext().log("===========================");
        
        // Sanitizar mensaje para usuario final
        String userMessage = sanitizeErrorMessage(e.getMessage());
        request.setAttribute("error", userMessage);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }
}
