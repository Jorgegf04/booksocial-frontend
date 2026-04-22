package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booksocial_frontend.dto.EventResponseDTO;
import com.example.booksocial_frontend.service.EventClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Controlador MVC del módulo de eventos de BookSocial.
 *
 * <p>Los eventos son actividades exclusivas para usuarios suscritos.
 * Este controlador lista los próximos eventos obtenidos del backend
 * y permite a los usuarios suscritos unirse o abandonar un evento.</p>
 *
 * <p>Estructura visual de la página:</p>
 * <ul>
 *   <li>Primer evento → tarjeta destacada (featured).</li>
 *   <li>Segundo evento → tarjeta secundaria.</li>
 *   <li>Resto → listado de próximos eventos.</li>
 * </ul>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsController {

  private final EventClientService eventService;

  @GetMapping
  public String listEvents(Model model, HttpSession session) {
    try {
      List<EventResponseDTO> events = eventService.getUpcomingEvents();

      // Separar featured (primero) del resto
      EventResponseDTO featured = events.isEmpty() ? null : events.get(0);
      EventResponseDTO secondary = events.size() > 1 ? events.get(1) : null;
      List<EventResponseDTO> upcoming = events.size() > 2 ? events.subList(2, events.size()) : List.of();

      model.addAttribute("featured", featured);
      model.addAttribute("secondary", secondary);
      model.addAttribute("upcoming", upcoming);
    } catch (Exception e) {
      model.addAttribute("featured", null);
      model.addAttribute("secondary", null);
      model.addAttribute("upcoming", List.of());
    }

    Long userId = (Long) session.getAttribute("userId");
    model.addAttribute("sessionUserId", userId);

    return "events/index";
  }

  @PostMapping("/{id}/join")
  public String join(@PathVariable Long id, HttpSession session,
                     RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      eventService.joinEvent(id, userId);
      ra.addFlashAttribute("successMsg", "Te has unido al evento correctamente.");
    } catch (Exception e) {
      ra.addFlashAttribute("errorMsg", e.getMessage());
    }
    return "redirect:/events";
  }

  @PostMapping("/{id}/leave")
  public String leave(@PathVariable Long id, HttpSession session,
                      RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      eventService.leaveEvent(id, userId);
      ra.addFlashAttribute("successMsg", "Has abandonado el evento.");
    } catch (Exception e) {
      ra.addFlashAttribute("errorMsg", e.getMessage());
    }
    return "redirect:/events";
  }
}
