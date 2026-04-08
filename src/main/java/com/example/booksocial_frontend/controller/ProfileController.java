package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.service.UserClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class ProfileController {

  private final UserClientService userService;

  /** Perfil público de cualquier usuario */
  @GetMapping("/{id}")
  public String showProfile(@PathVariable Long id, HttpSession session, Model model) {

    UserResponseDTO user = userService.getUserById(id);
    List<TrackingWorkResponseDTO> tracking = userService.getTrackingByUser(id);

    Long sessionUserId = (Long) session.getAttribute("userId");
    boolean isOwnProfile = sessionUserId != null && sessionUserId.equals(id);

    model.addAttribute("user", user);
    model.addAttribute("tracking", tracking);
    model.addAttribute("isOwnProfile", isOwnProfile);
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
