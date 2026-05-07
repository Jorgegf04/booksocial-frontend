package com.example.booksocial_frontend.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.exception.ApiErrorUtils;
import com.example.booksocial_frontend.service.UserClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador MVC de la página de Comunidad de BookSocial.
 *
 * <p>
 * Muestra un grid con todos los usuarios registrados (excluyendo al usuario
 * actual) y permite seguirlos o dejar de seguirlos directamente desde la vista.
 * </p>
 *
 * <p>
 * Para cada usuario de la lista el controlador carga el conjunto de IDs a los
 * que el usuario en sesión ya sigue ({@code followingIds}), de forma que la
 * plantilla puede renderizar el botón correcto sin lógica extra en el cliente.
 * </p>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

  private final UserClientService userService;

  @GetMapping
  public String index(HttpSession session, Model model) {
    Long sessionUserId = (Long) session.getAttribute("userId");

    List<UserResponseDTO> users;
    try {
      users = userService.getAllUsers().stream()
          .filter(u -> sessionUserId == null || !u.getId().equals(sessionUserId))
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.warn("[COMMUNITY] Error al cargar usuarios: {}", e.getMessage());
      users = List.of();
    }

    Set<Long> followingIds = Set.of();
    if (sessionUserId != null) {
      try {
        followingIds = userService.getFollowing(sessionUserId).stream()
            .map(UserResponseDTO::getId)
            .collect(Collectors.toSet());
      } catch (Exception e) {
        log.warn("[COMMUNITY] Error al cargar seguidos de userId={}: {}", sessionUserId, e.getMessage());
        followingIds = Set.of();
      }
    }

    model.addAttribute("users", users);
    model.addAttribute("followingIds", followingIds);
    model.addAttribute("sessionUserId", sessionUserId);

    return "community/index";
  }

  @PostMapping("/{id}/follow")
  public String follow(@PathVariable Long id, HttpSession session,
                       org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null)
      return "redirect:/auth/login";
    try {
      userService.followUser(sessionUserId, id);
    } catch (Exception e) {
      log.warn("[COMMUNITY] Error al seguir usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/community";
  }

  @PostMapping("/{id}/unfollow")
  public String unfollow(@PathVariable Long id, HttpSession session,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null)
      return "redirect:/auth/login";
    try {
      userService.unfollowUser(sessionUserId, id);
    } catch (Exception e) {
      log.warn("[COMMUNITY] Error al dejar de seguir usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/community";
  }
}
