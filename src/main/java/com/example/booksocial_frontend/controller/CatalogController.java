package com.example.booksocial_frontend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.booksocial_frontend.dto.WorkFilterDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.service.WorkClientService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador MVC encargado de la gestión de la vista del catálogo.
 *
 * <p>
 * Este controlador actúa como intermediario entre la vista Thymeleaf
 * y el backend REST, permitiendo mostrar y filtrar obras.
 * </p>
 *
 * <p>
 * Responsabilidades:
 * </p>
 * <ul>
 * <li>Cargar catálogo completo</li>
 * <li>Aplicar filtros dinámicos</li>
 * <li>Paginación del catálogo</li>
 * <li>Enviar datos a la vista</li>
 * </ul>
 *
 * @author Jorge
 * @since 2026
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/catalog")
public class CatalogController {

  private static final int PAGE_SIZE = 10;

  private final WorkClientService workService;

  /**
   * Muestra el catálogo completo de obras.
   *
   * @param page  página actual (0-indexed)
   * @param model modelo de la vista
   * @return vista catalog.html
   */
  @GetMapping
  public String showCatalog(@RequestParam(defaultValue = "0") int page, Model model) {

    List<WorkResponseDTO> works = List.of();
    try {
      works = workService.getAllWorks();
    } catch (Exception e) {
      model.addAttribute("apiError", "No se pudo conectar con el servidor. Verifica que el backend esté activo.");
    }

    applyPaginationToModel(works, page, new WorkFilterDTO(), model);
    return "work/catalog";
  }

  /**
   * Aplica filtros al catálogo con soporte de paginación.
   *
   * <p>
   * Obtiene todas las obras y aplica todos los filtros localmente para
   * garantizar búsqueda case-insensitive y control total sobre el filtrado.
   * </p>
   *
   * @param filter DTO con filtros
   * @param page   página actual (0-indexed)
   * @param model  modelo de la vista
   * @return vista filtrada y paginada
   */
  @GetMapping("/filter")
  public String filterCatalog(@ModelAttribute WorkFilterDTO filter,
      @RequestParam(defaultValue = "0") int page,
      Model model) {

    // Normalizar título: trim + null si vacío
    String title = filter.getTitle();
    if (title != null) {
      title = title.trim();
      filter.setTitle(title.isEmpty() ? null : title);
    }

    List<WorkResponseDTO> works = List.of();
    try {
      works = workService.getAllWorks();
      works = applyAllFilters(works, filter);
    } catch (Exception e) {
      model.addAttribute("apiError", "No se pudo conectar con el servidor. Verifica que el backend esté activo.");
    }

    applyPaginationToModel(works, page, filter, model);
    return "work/catalog";
  }

  // ==========================================
  // FILTRADO LOCAL COMPLETO
  // ==========================================

  /**
   * Aplica todos los filtros localmente sobre la lista completa de obras.
   * El título se busca de forma case-insensitive y por subcadena.
   */
  private List<WorkResponseDTO> applyAllFilters(List<WorkResponseDTO> works, WorkFilterDTO filter) {
    String titleLower = filter.getTitle() != null ? filter.getTitle().toLowerCase() : null;

    return works.stream()
        .filter(w -> titleLower == null
            || (w.getTitle() != null && w.getTitle().toLowerCase().contains(titleLower)))
        .filter(w -> filter.getGenre() == null
            || (w.getGenre() != null && w.getGenre().name().equals(filter.getGenre().name())))
        .filter(w -> filter.getType() == null
            || (w.getType() != null && w.getType().name().equals(filter.getType().name())))
        .filter(w -> filter.getDemographic() == null
            || (w.getDemographic() != null && w.getDemographic().name().equals(filter.getDemographic().name())))
        .filter(w -> filter.getMinRating() == null || filter.getMinRating() == 0
            || (w.getAverageRating() != null && w.getAverageRating() >= filter.getMinRating()))
        .filter(w -> filter.getPublishedAfter() == null
            || (w.getPublicationDate() != null && !w.getPublicationDate().isBefore(filter.getPublishedAfter())))
        .filter(w -> filter.getPublishedBefore() == null
            || (w.getPublicationDate() != null && !w.getPublicationDate().isAfter(filter.getPublishedBefore())))
        .collect(Collectors.toList());
  }

  // ==========================================
  // PAGINACIÓN
  // ==========================================

  /**
   * Aplica la paginación a la lista completa y añade los atributos al modelo.
   */
  private void applyPaginationToModel(List<WorkResponseDTO> allWorks, int page,
      WorkFilterDTO filter, Model model) {

    int total = allWorks.size();
    int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
    int safePage = Math.max(0, Math.min(page, totalPages - 1));

    int fromIndex = safePage * PAGE_SIZE;
    int toIndex = Math.min(fromIndex + PAGE_SIZE, total);
    List<WorkResponseDTO> pageWorks = total == 0 ? List.of() : allWorks.subList(fromIndex, toIndex);

    model.addAttribute("works", pageWorks);
    model.addAttribute("total", total);
    model.addAttribute("currentPage", safePage);
    model.addAttribute("totalPages", totalPages);
    model.addAttribute("pageNumbers", buildPageNumbers(safePage, totalPages));
    model.addAttribute("filter", filter);
  }

  /**
   * Construye la lista de páginas a mostrar en la paginación.
   * El valor -1 representa un separador de elipsis ("...").
   */
  private List<Integer> buildPageNumbers(int current, int total) {
    List<Integer> pages = new ArrayList<>();
    if (total <= 7) {
      for (int i = 0; i < total; i++) {
        pages.add(i);
      }
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
