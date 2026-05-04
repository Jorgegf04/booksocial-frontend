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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Controlador MVC que gestiona el flujo de autenticación del frontend.
 *
 * <p>
 * Delega las operaciones de login y registro en
 * {@link com.example.booksocial_frontend.service.AuthClientService},
 * que realiza las llamadas REST al backend. Tras un login exitoso almacena en
 * la
 * sesión HTTP los atributos {@code JWT}, {@code userId}, {@code username} y
 * {@code role}
 * que el resto de controladores utilizan para personalizar la vista.
 * </p>
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

  /**
   * Endpoint que muestra la la vista de login en la pagina web
   *
   * <p>
   * Con el endpoint nos redirice a la pagina de login para poder iniciar sesión
   * en la web
   * </p>
   * 
   *
   * @author Jorge
   * @version 1.4
   * @since 2026-04-22
   */
  @GetMapping("/login")
  public String loginPage(Model model) {
    model.addAttribute("loginRequest", new LoginRequestDTO());
    return "auth/login";
  }

  /**
   * Endpoint post que devuelve el DTO para poder iniciar sesión
   *
   * <p>
   * Con este endpoint podemos inciar sesión en la pagina web
   * </p>
   * 
   *
   * @author Jorge
   * @version 1.4
   * @since 2026-04-22
   */
  @PostMapping("/login")
  public String login(
      @ModelAttribute LoginRequestDTO request,
      HttpSession session,
      HttpServletResponse response,
      Model model) {

    try {
      JwtResponseDTO jwt = authClientService.login(request);

      session.setAttribute("JWT", jwt.getToken());
      session.setAttribute("userId", jwt.getUserId());
      session.setAttribute("username", jwt.getUsername());
      session.setAttribute("role", jwt.getRole());

      // Persistir sesión en cookies durante 7 días para que survive a reinicios
      int maxAge = 7 * 24 * 60 * 60;
      addCookie(response, "bs_jwt", jwt.getToken(), maxAge, true);
      addCookie(response, "bs_uid", String.valueOf(jwt.getUserId()), maxAge, false);
      addCookie(response, "bs_usr", jwt.getUsername(), maxAge, false);
      addCookie(response, "bs_role", jwt.getRole(), maxAge, false);

      return "redirect:/catalog";

    } catch (Exception e) {
      model.addAttribute("error", "Credenciales incorrectas");
      return "auth/login";
    }
  }

  /**
   * Endpoint para cerrar sesión en la web, tambien borra las cookies de sesión de
   * la web
   * <p>
   * Endpoint para cerrar sesión en la web, tambien borra las cookies de sesión de
   * la web
   * </p>
   * 
   *
   * @author Jorge
   * @version 1.4
   * @since 2026-04-22
   */
  @GetMapping("/logout")
  public String logout(HttpSession session, HttpServletResponse response) {
    session.invalidate();
    // Borrar las cookies de autenticación
    for (String name : new String[] { "bs_jwt", "bs_uid", "bs_usr", "bs_role" }) {
      addCookie(response, name, "", 0, name.equals("bs_jwt"));
    }
    return "redirect:/auth/login";
  }

  /**
   * Metodo para crear una cookie para guardar los usuarios
   * <p>
   * Metodo para crear una cookie para guardar los usuarios
   * </p>
   * 
   *
   * @author Jorge
   * @version 1.4
   * @since 2026-04-22
   */
  private void addCookie(HttpServletResponse response, String name, String value,
      int maxAge, boolean httpOnly) {
    Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(maxAge);
    cookie.setPath("/");
    cookie.setHttpOnly(httpOnly);
    response.addCookie(cookie);
  }

  /**
   * Endpoint para poder acceder a la vista de registro de sesión
   * <p>
   * Metodo para crear una cookie para guardar los usuarios
   * </p>
   * 
   *
   * @author Jorge
   * @version 1.4
   * @since 2026-04-22
   */
  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("registerRequest", new RegisterRequestDTO());
    return "auth/register";
  }

  /**
   * Endpoint para poder registrarte en la web y envia esos datos en una request
   * DTO
   * <p>
   * Endpoint para poder registrarte en la web y envia esos datos en una request
   * DTO
   * </p>
   * 
   *
   * @author Jorge
   * @version 1.4
   * @since 2026-04-22
   */
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