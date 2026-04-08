package com.example.booksocial_frontend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booksocial_frontend.dto.CartItemDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.service.CartService;
import com.example.booksocial_frontend.service.OrderClientService;
import com.example.booksocial_frontend.service.ProductClientService;
import com.example.booksocial_frontend.service.WorkClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

  private final CartService cartService;
  private final ProductClientService productService;
  private final WorkClientService workService;
  private final OrderClientService orderService;

  /** Ver carrito */
  @GetMapping
  public String viewCart(HttpSession session, Model model) {
    List<CartItemDTO> items = cartService.getCart(session);
    double subtotal = cartService.getTotal(session);
    double shipping = items.isEmpty() ? 0.0 : 5.50;
    double total = subtotal + shipping;

    model.addAttribute("cartItems", items);
    model.addAttribute("subtotal", subtotal);
    model.addAttribute("shipping", shipping);
    model.addAttribute("total", total);

    // Sugerencias: productos disponibles (excluir los ya en carrito)
    try {
      List<Long> inCart = items.stream().map(CartItemDTO::getProductId).toList();
      List<ProductResponseDTO> suggestions = productService.getAvailableProducts()
          .stream()
          .filter(p -> !inCart.contains(p.getId()))
          .limit(4)
          .toList();
      model.addAttribute("suggestions", suggestions);
    } catch (Exception e) {
      model.addAttribute("suggestions", List.of());
    }

    return "cart/index";
  }

  /** Añadir producto al carrito desde detalle de obra */
  @PostMapping("/add")
  public String addToCart(@RequestParam Long productId,
                          @RequestParam(defaultValue = "1") int quantity,
                          HttpSession session,
                          RedirectAttributes ra) {
    try {
      ProductResponseDTO product = productService.getProductById(productId);

      if (product.getStock() < quantity) {
        ra.addFlashAttribute("cartError", "Stock insuficiente.");
        return "redirect:/work/" + product.getWorkId();
      }

      // Obtener img de la obra
      String img = null;
      try {
        WorkResponseDTO work = workService.getWorkById(product.getWorkId());
        img = work.getImg();
      } catch (Exception ignored) {}

      CartItemDTO item = new CartItemDTO(
          product.getId(),
          product.getWorkTitle(),
          product.getEditionTitle() != null ? product.getEditionTitle() : "Edición " + product.getEditionId(),
          product.getEditorialName(),
          img,
          product.getPrice(),
          quantity
      );

      cartService.addItem(session, item);
      ra.addFlashAttribute("cartSuccess", "Producto añadido al carrito.");
    } catch (Exception e) {
      ra.addFlashAttribute("cartError", "No se pudo añadir el producto.");
    }
    return "redirect:/cart";
  }

  /** Eliminar ítem del carrito */
  @PostMapping("/remove/{productId}")
  public String removeFromCart(@PathVariable Long productId, HttpSession session) {
    cartService.removeItem(session, productId);
    return "redirect:/cart";
  }

  /** Actualizar cantidad */
  @PostMapping("/update")
  public String updateQuantity(@RequestParam Long productId,
                               @RequestParam int quantity,
                               HttpSession session) {
    cartService.updateQuantity(session, productId, quantity);
    return "redirect:/cart";
  }

  /** Confirmar compra → crea el pedido */
  @PostMapping("/checkout")
  public String checkout(HttpSession session, RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    List<CartItemDTO> items = cartService.getCart(session);
    if (items.isEmpty()) {
      ra.addFlashAttribute("cartError", "El carrito está vacío.");
      return "redirect:/cart";
    }

    try {
      List<Map<String, Object>> lines = items.stream()
          .map(i -> Map.<String, Object>of(
              "productId", i.getProductId(),
              "quantity", i.getQuantity()
          ))
          .toList();

      orderService.createOrder(userId, lines);
      cartService.clearCart(session);
      ra.addFlashAttribute("orderSuccess", true);
      return "redirect:/orders";
    } catch (Exception e) {
      ra.addFlashAttribute("cartError", "Error al procesar el pedido: " + e.getMessage());
      return "redirect:/cart";
    }
  }
}
