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
import com.example.booksocial_frontend.service.UserClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

  private final UserClientService userService;

  @GetMapping
  public String index(HttpSession session, Model model) {
    Long sessionUserId = (Long) session.getAttribute("userId");

    List<UserResponseDTO> users = userService.getAllUsers().stream()
        .filter(u -> sessionUserId == null || !u.getId().equals(sessionUserId))
        .collect(Collectors.toList());

    Set<Long> followingIds = Set.of();
    if (sessionUserId != null) {
      followingIds = userService.getFollowing(sessionUserId).stream()
          .map(UserResponseDTO::getId)
          .collect(Collectors.toSet());
    }

    model.addAttribute("users", users);
    model.addAttribute("followingIds", followingIds);
    model.addAttribute("sessionUserId", sessionUserId);

    return "community/index";
  }

  @PostMapping("/{id}/follow")
  public String follow(@PathVariable Long id, HttpSession session) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null) return "redirect:/auth/login";
    userService.followUser(sessionUserId, id);
    return "redirect:/community";
  }

  @PostMapping("/{id}/unfollow")
  public String unfollow(@PathVariable Long id, HttpSession session) {
    Long sessionUserId = (Long) session.getAttribute("userId");
    if (sessionUserId == null) return "redirect:/auth/login";
    userService.unfollowUser(sessionUserId, id);
    return "redirect:/community";
  }
}
