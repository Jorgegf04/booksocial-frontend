package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.service.OrderClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

  private final OrderClientService orderService;

  @GetMapping
  public String myOrders(HttpSession session, Model model) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      List<OrderResponseDTO> orders = orderService.getOrdersByUser(userId);
      model.addAttribute("orders", orders);
    } catch (Exception e) {
      model.addAttribute("orders", List.of());
    }

    return "orders/history";
  }
}
