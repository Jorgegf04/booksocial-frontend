package com.example.booksocial_frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.booksocial_frontend.dto.JwtResponseDTO;
import com.example.booksocial_frontend.dto.LoginRequestDTO;
import com.example.booksocial_frontend.dto.RegisterRequestDTO;
import com.example.booksocial_frontend.service.AuthClientService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Controlador MVC que gestiona el flujo de autenticación del frontend.
 *
 * <p>Delega las operaciones de login y registro en {@link com.example.booksocial_frontend.service.AuthClientService},
 * que realiza las llamadas REST al backend. Tras un login exitoso almacena en la
 * sesión HTTP los atributos {@code JWT}, {@code userId}, {@code username} y {@code role}
 * que el resto de controladores utilizan para personalizar la vista.</p>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthClientService authClientService;

  @GetMapping("/login")
  public String loginPage(Model model) {
    model.addAttribute("loginRequest", new LoginRequestDTO());
    return "auth/login";
  }

  @PostMapping("/login")
  public String login(
      @ModelAttribute LoginRequestDTO request,
      HttpSession session,
      Model model) {

    try {
      JwtResponseDTO response = authClientService.login(request);

      session.setAttribute("JWT", response.getToken());
      session.setAttribute("userId", response.getUserId());
      session.setAttribute("username", response.getUsername());
      session.setAttribute("role", response.getRole());

      return "redirect:/catalog";

    } catch (Exception e) {
      model.addAttribute("error", "Credenciales incorrectas");
      return "auth/login";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/auth/login";
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("registerRequest", new RegisterRequestDTO());
    return "auth/register";
  }

  @PostMapping("/register")
  public String register(
      @ModelAttribute RegisterRequestDTO request,
      RedirectAttributes ra,
      Model model) {

    try {
      authClientService.register(request);
      ra.addFlashAttribute("success", "¡Cuenta creada! Ya puedes iniciar sesión.");
      return "redirect:/auth/login";
    } catch (Exception e) {
      model.addAttribute("registerRequest", request);
      model.addAttribute("error", "Error al registrarse: " + e.getMessage());
      return "auth/register";
    }
  }
}