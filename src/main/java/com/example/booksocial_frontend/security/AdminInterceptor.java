package com.example.booksocial_frontend.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Interceptor de seguridad que protege todas las rutas del panel de administración.
 *
 * <p>Verifica en el {@code preHandle} que la sesión HTTP esté activa y que el
 * atributo {@code role} sea {@code "ADMIN"}. Si la verificación falla, redirige
 * al usuario a la página de login y cancela el procesamiento de la petición.</p>
 *
 * <p>Está registrado en {@link WebMvcConfig} sobre el patrón {@code /admin/**}.</p>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute("role") == null
        || !"ADMIN".equals(session.getAttribute("role"))) {
      response.sendRedirect("/auth/login");
      return false;
    }

    return true;
  }
}
