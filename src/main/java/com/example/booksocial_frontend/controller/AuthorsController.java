package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.booksocial_frontend.dto.AuthorResponseDTO;
import com.example.booksocial_frontend.service.AuthorClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorsController {

  private final AuthorClientService authorService;

  @GetMapping
  public String index(@RequestParam(required = false) String q,
                      HttpSession session, Model model) {
    List<AuthorResponseDTO> authors;
    try {
      if (q != null && !q.isBlank()) {
        authors = authorService.getAllAuthors().stream()
            .filter(a -> a.getName().toLowerCase().contains(q.toLowerCase()))
            .toList();
      } else {
        authors = authorService.getAllAuthors();
      }
    } catch (Exception e) {
      authors = List.of();
    }

    model.addAttribute("authors", authors);
    model.addAttribute("q", q);
    model.addAttribute("sessionUserId", session.getAttribute("userId"));

    return "author/list";
  }
}
