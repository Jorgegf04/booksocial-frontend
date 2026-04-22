package com.example.booksocial_frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación frontend de BookSocial.
 *
 * <p>Arranca el contexto Spring Boot que sirve las vistas Thymeleaf en el
 * puerto 8080. Actúa como cliente REST del backend (puerto 9999),
 * delegando en los servicios {@code *ClientService} la comunicación con la API.</p>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@SpringBootApplication
public class BooksocialFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooksocialFrontendApplication.class, args);
	}

}
