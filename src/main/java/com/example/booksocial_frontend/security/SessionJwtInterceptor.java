package com.example.booksocial_frontend.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 * Interceptor que propaga el JWT de la sesión HTTP al header Authorization de
 * cada petición RestClient hacia el backend.
 *
 * <p>Sin este interceptor los endpoints protegidos con {@code @PreAuthorize("hasRole('ADMIN')")}
 * devuelven 403 porque el RestClient no envía credenciales.</p>
 */
@Component
public class SessionJwtInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpSession session = attrs.getRequest().getSession(false);
            if (session != null) {
                String jwt = (String) session.getAttribute("JWT");
                if (jwt != null && !jwt.isBlank()) {
                    request.getHeaders().setBearerAuth(jwt);
                }
            }
        }
        return execution.execute(request, body);
    }
}
