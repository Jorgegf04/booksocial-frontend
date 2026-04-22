package com.example.booksocial_frontend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * Configuración MVC del frontend que registra los interceptores de la aplicación.
 *
 * <p>Añade {@link AdminInterceptor} sobre el patrón {@code /admin/**} para
 * garantizar que únicamente usuarios con rol ADMIN puedan acceder al panel
 * de administración.</p>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final AdminInterceptor adminInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(adminInterceptor)
        .addPathPatterns("/admin/**");
  }
}
