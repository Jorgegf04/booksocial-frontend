package com.example.booksocial_frontend.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
