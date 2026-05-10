package com.example.booksocial_frontend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.booksocial_frontend.dto.AuthorResponseDTO;
import com.example.booksocial_frontend.service.AuthorClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorsController {

  private static final int PAGE_SIZE = 12;

  private final AuthorClientService authorService;

  @GetMapping
  public String index(@RequestParam(required = false) String q,
                      @RequestParam(required = false) String nationality,
                      @RequestParam(defaultValue = "0") int page,
                      HttpSession session, Model model) {

    List<AuthorResponseDTO> allAuthors;
    try {
      allAuthors = authorService.getAllAuthors();
    } catch (Exception e) {
      log.warn("[AUTHORS] Error al cargar autores: {}", e.getMessage());
      allAuthors = List.of();
    }

    // Extraer lista de nacionalidades únicas para el select
    List<String> nationalities = allAuthors.stream()
        .map(AuthorResponseDTO::getNationality)
        .filter(n -> n != null && !n.isBlank())
        .distinct()
        .sorted()
        .collect(Collectors.toList());

    // Filtrar por nombre
    List<AuthorResponseDTO> filtered = allAuthors;
    if (q != null && !q.isBlank()) {
      String qLower = q.toLowerCase();
      filtered = filtered.stream()
          .filter(a -> a.getName() != null && a.getName().toLowerCase().contains(qLower))
          .collect(Collectors.toList());
    }

    // Filtrar por nacionalidad
    if (nationality != null && !nationality.isBlank()) {
      filtered = filtered.stream()
          .filter(a -> nationality.equals(a.getNationality()))
          .collect(Collectors.toList());
    }

    // Paginación en memoria
    int total = filtered.size();
    int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
    int safePage = Math.max(0, Math.min(page, totalPages - 1));
    int from = safePage * PAGE_SIZE;
    int to = Math.min(from + PAGE_SIZE, total);
    List<AuthorResponseDTO> pageAuthors = total == 0 ? List.of() : filtered.subList(from, to);

    model.addAttribute("authors", pageAuthors);
    model.addAttribute("q", q);
    model.addAttribute("nationality", nationality);
    model.addAttribute("nationalities", nationalities);
    model.addAttribute("currentPage", safePage);
    model.addAttribute("totalPages", totalPages);
    model.addAttribute("pageNumbers", buildPageNumbers(safePage, totalPages));
    model.addAttribute("sessionUserId", session.getAttribute("userId"));

    return "author/list";
  }

  private List<Integer> buildPageNumbers(int current, int total) {
    List<Integer> pages = new ArrayList<>();
    if (total <= 7) {
      for (int i = 0; i < total; i++) pages.add(i);
      return pages;
    }
    pages.add(0);
    int start = Math.max(1, current - 2);
    int end = Math.min(total - 2, current + 2);
    if (start > 1) pages.add(-1);
    for (int i = start; i <= end; i++) pages.add(i);
    if (end < total - 2) pages.add(-1);
    pages.add(total - 1);
    return pages;
  }
}
