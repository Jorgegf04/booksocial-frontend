package com.example.booksocial_frontend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro que restaura la sesión HTTP del usuario a partir de cookies persistentes.
 *
 * <p>Cuando el servidor reinicia o la sesión HTTP expira, el usuario pierde su estado
 * de autenticación aunque el JWT siga siendo válido. Este filtro intercepta cada petición
 * y, si la sesión no contiene {@code userId}, intenta recuperarla leyendo las cookies
 * {@code bs_jwt}, {@code bs_uid}, {@code bs_usr} y {@code bs_role} que se establecen
 * al hacer login con una caducidad de 7 días.</p>
 *
 * <p>De este modo el usuario permanece autenticado en el navegador durante 7 días
 * sin tener que volver a introducir sus credenciales.</p>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-23
 */
@Component
@Order(1)
public class SessionRestoreFilter implements Filter {

  private static final String COOKIE_JWT  = "bs_jwt";
  private static final String COOKIE_UID  = "bs_uid";
  private static final String COOKIE_USR  = "bs_usr";
  private static final String COOKIE_ROLE = "bs_role";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute("userId") == null) {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        String jwt = null, uid = null, usr = null, role = null;

        for (Cookie c : cookies) {
          switch (c.getName()) {
            case COOKIE_JWT  -> jwt  = c.getValue();
            case COOKIE_UID  -> uid  = c.getValue();
            case COOKIE_USR  -> usr  = c.getValue();
            case COOKIE_ROLE -> role = c.getValue();
          }
        }

        if (jwt != null && uid != null && !jwt.isBlank() && !uid.isBlank()) {
          HttpSession s = request.getSession(true);
          s.setAttribute("JWT",      jwt);
          s.setAttribute("userId",   Long.parseLong(uid));
          s.setAttribute("username", usr);
          s.setAttribute("role",     role);
        }
      }
    }

    chain.doFilter(req, res);
  }
}
