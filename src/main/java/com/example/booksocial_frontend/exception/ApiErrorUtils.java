package com.example.booksocial_frontend.exception;

import org.springframework.web.client.HttpStatusCodeException;

/**
 * Extrae el mensaje legible del cuerpo JSON de error que devuelve el backend.
 *
 * El backend serializa los errores como {"status":400,"message":"..."}.
 * Esta clase evita mostrar al usuario el objeto JSON completo o el prefijo
 * HTTP del status (ej: "400 Bad Request: {...}").
 */
public final class ApiErrorUtils {

    private ApiErrorUtils() {}

    public static String extractApiError(Exception e) {
        if (e instanceof HttpStatusCodeException httpEx) {
            try {
                String body = httpEx.getResponseBodyAsString();
                if (body != null && body.contains("\"message\"")) {
                    int start = body.indexOf("\"message\"") + 11;
                    int end = body.indexOf("\"", start);
                    if (start > 10 && end > start) {
                        return body.substring(start, end);
                    }
                }
            } catch (Exception ignored) {}
            // Recorta el prefijo de status HTTP: "400 Bad Request: {...}"
            String msg = httpEx.getMessage();
            if (msg == null) return "Error desconocido";
            int colonIdx = msg.indexOf(": {");
            return colonIdx > 0 ? msg.substring(0, colonIdx) : msg;
        }
        String msg = e.getMessage();
        return msg != null ? msg : "Error desconocido";
    }
}
