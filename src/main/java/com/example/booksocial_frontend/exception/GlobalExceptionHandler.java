package com.example.booksocial_frontend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Manejador global de excepciones para el frontend MVC.
 *
 * Captura excepciones que se propagan fuera de los controladores y las
 * convierte en vistas de error amigables. Diferencia entre:
 * - Backend no disponible (ResourceAccessException)
 * - Error en el backend (4xx/5xx HTTP)
 * - Error inesperado genérico
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Backend completamente inaccesible (connection refused, timeout, etc.)
     */
    @ExceptionHandler(ResourceAccessException.class)
    public String handleConnectionError(
            ResourceAccessException ex,
            Model model,
            HttpServletResponse response,
            HttpServletRequest request) {

        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        model.addAttribute("errorStatus", 503);
        model.addAttribute("errorTitle", "Servicio no disponible");
        model.addAttribute("errorMessage",
                "No se puede conectar con el servidor. Verifique que el backend esté activo e inténtelo de nuevo.");
        model.addAttribute("errorPath", request.getRequestURI());
        return "errors/general";
    }

    /**
     * El backend respondió con un error 4xx (recurso no encontrado, etc.)
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public String handleClientError(
            HttpClientErrorException ex,
            Model model,
            HttpServletResponse response,
            HttpServletRequest request) {

        int status = ex.getStatusCode().value();
        response.setStatus(status);
        model.addAttribute("errorStatus", status);
        model.addAttribute("errorTitle", status == 404 ? "No encontrado" : "Error en la solicitud");
        model.addAttribute("errorMessage",
                status == 404
                        ? "El recurso que buscas no existe."
                        : "La solicitud no pudo procesarse: " + ex.getStatusText());
        model.addAttribute("errorPath", request.getRequestURI());
        return "errors/general";
    }

    /**
     * El backend respondió con un error 5xx (fallo interno del servidor API)
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public String handleServerError(
            HttpServerErrorException ex,
            Model model,
            HttpServletResponse response,
            HttpServletRequest request) {

        response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        model.addAttribute("errorStatus", 502);
        model.addAttribute("errorTitle", "Error en el servidor");
        model.addAttribute("errorMessage",
                "El servidor ha encontrado un error interno. Por favor, inténtelo más tarde.");
        model.addAttribute("errorPath", request.getRequestURI());
        return "errors/general";
    }

    /**
     * Cualquier otra excepción no controlada.
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneric(
            Exception ex,
            Model model,
            HttpServletResponse response,
            HttpServletRequest request) {

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("errorStatus", 500);
        model.addAttribute("errorTitle", "Error inesperado");
        model.addAttribute("errorMessage",
                "Ha ocurrido un error inesperado. Por favor, inténtelo más tarde.");
        model.addAttribute("errorPath", request.getRequestURI());
        return "errors/general";
    }
}
