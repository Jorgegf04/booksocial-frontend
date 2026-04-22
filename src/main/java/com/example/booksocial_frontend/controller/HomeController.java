package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.service.WorkClientService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador MVC de la página de inicio de BookSocial.
 *
 * <p>Carga un máximo de 4 obras destacadas del catálogo para mostrar
 * en la sección hero de la portada. Si el backend no está disponible,
 * la lista se devuelve vacía sin lanzar error al usuario.</p>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

  private final WorkClientService workService;

  @GetMapping("/")
  public String home(Model model) {
    try {
      List<WorkResponseDTO> works = workService.getAllWorks();
      model.addAttribute("featuredWorks", works.stream().limit(4).toList());
    } catch (Exception e) {
      model.addAttribute("featuredWorks", List.of());
    }
    return "home";
  }
}
