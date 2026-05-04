package com.example.booksocial_frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booksocial_frontend.dto.AuthorResponseDTO;
import com.example.booksocial_frontend.service.AuthorClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/author")
public class AuthorDetailController {

  private final AuthorClientService authorService;

  @GetMapping("/{id}")
  public String showAuthor(@PathVariable Long id, HttpSession session, Model model) {
    AuthorResponseDTO author;
    try {
      author = authorService.getAuthorById(id);
    } catch (Exception e) {
      return "redirect:/catalog";
    }

    Long userId = (Long) session.getAttribute("userId");
    String role  = (String) session.getAttribute("role");
    boolean following = false;
    if (userId != null) {
      following = authorService.isFollowing(id, userId);
    }

    model.addAttribute("author", author);
    model.addAttribute("loggedIn", userId != null);
    model.addAttribute("sessionUserId", userId);
    model.addAttribute("sessionRole", role);
    model.addAttribute("following", following);

    return "author/detail";
  }

  @PostMapping("/{id}/follow")
  public String follow(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      authorService.followAuthor(id, userId);
      ra.addFlashAttribute("followSuccess",
          "¡Ahora sigues a este autor! Recibirás un email de confirmación.");
    } catch (Exception e) {
      ra.addFlashAttribute("followError", "No se pudo seguir al autor.");
    }
    return "redirect:/author/" + id;
  }

  @PostMapping("/{id}/unfollow")
  public String unfollow(@PathVariable Long id, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      authorService.unfollowAuthor(id, userId);
    } catch (Exception ignored) {}
    return "redirect:/author/" + id;
  }
}
