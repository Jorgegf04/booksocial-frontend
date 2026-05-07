package com.example.booksocial_frontend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.dto.TrackingOrderResponseDTO;
import com.example.booksocial_frontend.dto.TrackingWorkRequestDTO;
import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.exception.ApiErrorUtils;
import com.example.booksocial_frontend.service.FileUploadClientService;
import com.example.booksocial_frontend.service.OrderClientService;
import com.example.booksocial_frontend.service.TrackingOrderClientService;
import com.example.booksocial_frontend.service.TrackingWorkClientService;
import com.example.booksocial_frontend.service.UserClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador MVC del perfil de usuario de BookSocial.
 *
 * <p>Gestiona tanto el perfil propio como el perfil público de cualquier usuario.
 * Las funcionalidades varían según si el visitante es el propietario del perfil:</p>
 * <ul>
 *   <li><b>Perfil propio:</b> formulario de edición, lista de pedidos y
 *       estadísticas personales.</li>
 *   <li><b>Perfil ajeno:</b> botón de seguir/dejar de seguir y biblioteca pública.</li>
 * </ul>
 *
 * <h3>Sistema de seguimiento</h3>
 * <p>Al cargar cualquier perfil se recuperan las listas de seguidores y seguidos
 * del usuario visitado. Si hay sesión activa, se comprueba si el usuario en sesión
 * está entre los seguidores para renderizar el botón correcto.
 * Los endpoints {@code POST /{id}/follow} y {@code POST /{id}/unfollow} delegan
 * en {@link com.example.booksocial_frontend.service.UserClientService}.</p>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class ProfileController {

  private final UserClientService userService;
  private final TrackingWorkClientService trackingService;
  private final OrderClientService orderService;
  private final TrackingOrderClientService trackingOrderService;
  private final FileUploadClientService fileUploadClientService;

  /** Perfil público de cualquier usuario */
  @GetMapping("/{id}")
  public String showProfile(@PathVariable Long id, HttpSession session, Model model) {

    UserResponseDTO user;
    try {
      user = userService.getUserById(id);
    } catch (Exception e) {
      return "redirect:/community";
    }

    List<TrackingWorkResponseDTO> tracking;
    try {
      tracking = userService.getTrackingByUser(id);
    } catch (Exception e) {
      tracking = List.of();
    }

    Long sessionUserId = (Long) session.getAttribute("userId");
    boolean isOwnProfile = sessionUserId != null && sessionUserId.equals(id);

    // Seguidores / siguiendo
    List<UserResponseDTO> followers;
    List<UserResponseDTO> following;
    try {
      followers = userService.getFollowers(id);
    } catch (Exception e) {
      followers = List.of();
    }
    try {
      following = userService.getFollowing(id);
    } catch (Exception e) {
      following = List.of();
    }
    boolean isFollowing = sessionUserId != null && followers.stream()
        .anyMatch(f -> f.getId().equals(sessionUserId));

    // Pedidos: solo para el propio perfil
    List<OrderResponseDTO> orders = null;
    Map<Long, TrackingOrderResponseDTO> orderTrackings = new HashMap<>();
    if (isOwnProfile) {
      try {
        orders = orderService.getOrdersByUser(id);
      } catch (Exception e) {
        log.warn("No se pudieron cargar los pedidos del usuario {}: {}", id, e.getMessage());
        orders = List.of();
      }
      for (OrderResponseDTO order : orders) {
        TrackingOrderResponseDTO t = trackingOrderService.getLatestByOrder(order.getId());
        if (t != null) {
          orderTrackings.put(order.getId(), t);
        }
      }
    }

    model.addAttribute("user", user);
    model.addAttribute("tracking", tracking);
    model.addAttribute("isOwnProfile", isOwnProfile);
    model.addAttribute("sessionUserId", sessionUserId);
    model.addAttribute("followers", followers);
    model.addAttribute("following", following);
    model.addAttribute("followersCount", followers.size());
    model.addAttribute("followingCount", following.size());
    model.addAttribute("isFollowing", isFollowing);
    model.addAttribute("orders", orders);
    model.addAttribute("orderTrackings", orderTrackings);
    model.addAttribute("updateForm", new UpdateUserRequestDTO(
        user.getUsername(), user.getEmail(),
        user.getName(), user.getSecondName(), user.getImg()));

    return "user/profile";
  }

  @PostMapping("/{id}/follow")
  public String follow(@PathVariable Long id, HttpSession session,
                       org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null) return "redirect:/auth/login";
    try {
      userService.followUser(sessionUserId, id);
    } catch (Exception e) {
      log.warn("[PROFILE] Error al seguir usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/user/" + id;
  }

  @PostMapping("/{id}/unfollow")
  public String unfollow(@PathVariable Long id, HttpSession session,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null) return "redirect:/auth/login";
    try {
      userService.unfollowUser(sessionUserId, id);
    } catch (Exception e) {
      log.warn("[PROFILE] Error al dejar de seguir usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/user/" + id;
  }

  /** Redirige al perfil del usuario logueado */
  @GetMapping("/me")
  public String myProfile(HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";
    return "redirect:/user/" + userId;
  }

  /** Avanza el estado del seguimiento: PENDING → READING → COMPLETED → PENDING */
  @PostMapping("/{userId}/tracking/{trackingId}/next-status")
  public String advanceTrackingStatus(@PathVariable Long userId,
                                       @PathVariable Long trackingId,
                                       HttpSession session,
                                       org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null || !sessionUserId.equals(userId)) {
      return "redirect:/auth/login";
    }

    try {
      List<TrackingWorkResponseDTO> trackings = userService.getTrackingByUser(userId);
      TrackingWorkResponseDTO current = trackings.stream()
          .filter(t -> t.getId().equals(trackingId))
          .findFirst()
          .orElse(null);

      if (current != null) {
        String nextStatus = getNextStatus(current.getStatus());
        trackingService.update(trackingId,
            new TrackingWorkRequestDTO(userId, current.getWorkId(), nextStatus));
      }
    } catch (Exception e) {
      log.warn("[PROFILE] Error al avanzar estado de tracking id={}: {}", trackingId, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }

    return "redirect:/user/" + userId;
  }

  private String getNextStatus(String current) {
    if (current == null) return "READING";
    return switch (current) {
      case "PENDING" -> "READING";
      case "READING" -> "COMPLETED";
      default -> "PENDING";
    };
  }

  /** Actualizar datos del perfil */
  @PostMapping("/{id}/update")
  public String updateProfile(@PathVariable Long id,
                               @ModelAttribute UpdateUserRequestDTO form,
                               @RequestParam(required = false) MultipartFile avatarFile,
                               HttpSession session,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null || !sessionUserId.equals(id)) {
      return "redirect:/auth/login";
    }

    try {
      if (avatarFile != null && !avatarFile.isEmpty()) {
        form.setImg(fileUploadClientService.uploadImage(avatarFile));
      }
      userService.updateUser(id, form);
      ra.addFlashAttribute("successMsg", "Perfil actualizado correctamente.");
    } catch (Exception e) {
      log.warn("[PROFILE] Error al actualizar usuario id={}: {}", id, e.getMessage());
      ra.addFlashAttribute("errorMsg", ApiErrorUtils.extractApiError(e));
    }
    return "redirect:/user/" + id;
  }
}
