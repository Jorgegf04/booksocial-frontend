package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.booksocial_frontend.dto.CommentResponseDTO;
import com.example.booksocial_frontend.dto.EditionResponseDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.service.CommentClientService;
import com.example.booksocial_frontend.service.EditionClientService;
import com.example.booksocial_frontend.service.ProductClientService;
import com.example.booksocial_frontend.service.WorkClientService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/work")
public class WorkDetailController {

  private final WorkClientService workService;
  private final EditionClientService editionService;
  private final CommentClientService commentService;
  private final ProductClientService productService;

  @GetMapping("/{id}")
  public String showWork(@PathVariable Long id, Model model) {

    WorkResponseDTO work = workService.getWorkById(id);
    List<EditionResponseDTO> editions = editionService.getEditionsByWork(id);
    List<CommentResponseDTO> comments = commentService.getRootCommentsByWork(id);

    // Cargar producto disponible para esta obra (el primero con stock)
    ProductResponseDTO buyProduct = null;
    try {
      List<ProductResponseDTO> products = productService.getProductsByWork(id);
      buyProduct = products.stream()
          .filter(p -> p.getStock() != null && p.getStock() > 0)
          .findFirst()
          .orElse(null);
    } catch (Exception ignored) {}

    model.addAttribute("work", work);
    model.addAttribute("editions", editions);
    model.addAttribute("comments", comments);
    model.addAttribute("commentCount", comments.size());
    model.addAttribute("buyProduct", buyProduct);

    return "work/detail";
  }
}
