package com.example.booksocial_frontend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.dto.TrackingOrderResponseDTO;
import com.example.booksocial_frontend.dto.TrackingWorkRequestDTO;
import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.service.OrderClientService;
import com.example.booksocial_frontend.service.TrackingOrderClientService;
import com.example.booksocial_frontend.service.TrackingWorkClientService;
import com.example.booksocial_frontend.service.UserClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class ProfileController {

  private final UserClientService userService;
  private final TrackingWorkClientService trackingService;
  private final OrderClientService orderService;
  private final TrackingOrderClientService trackingOrderService;

  /** Perfil público de cualquier usuario */
  @GetMapping("/{id}")
  public String showProfile(@PathVariable Long id, HttpSession session, Model model) {

    UserResponseDTO user = userService.getUserById(id);
    List<TrackingWorkResponseDTO> tracking = userService.getTrackingByUser(id);

    Long sessionUserId = (Long) session.getAttribute("userId");
    boolean isOwnProfile = sessionUserId != null && sessionUserId.equals(id);

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
    model.addAttribute("orders", orders);
    model.addAttribute("orderTrackings", orderTrackings);
    model.addAttribute("updateForm", new UpdateUserRequestDTO(
        user.getUsername(), user.getEmail(),
        user.getName(), user.getSecondName(), user.getImg()));

    return "user/profile";
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
                                       HttpSession session) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null || !sessionUserId.equals(userId)) {
      return "redirect:/auth/login";
    }

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
                               HttpSession session) {

    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null || !sessionUserId.equals(id)) {
      return "redirect:/auth/login";
    }

    userService.updateUser(id, form);
    return "redirect:/user/" + id + "?updated=true";
  }
}
