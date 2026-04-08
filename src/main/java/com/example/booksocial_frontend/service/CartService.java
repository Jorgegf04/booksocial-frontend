package com.example.booksocial_frontend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.booksocial_frontend.dto.CartItemDTO;

import jakarta.servlet.http.HttpSession;

@Service
public class CartService {

  private static final String CART_KEY = "cart";

  @SuppressWarnings("unchecked")
  public List<CartItemDTO> getCart(HttpSession session) {
    List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute(CART_KEY);
    if (cart == null) {
      cart = new ArrayList<>();
      session.setAttribute(CART_KEY, cart);
    }
    return cart;
  }

  public void addItem(HttpSession session, CartItemDTO item) {
    List<CartItemDTO> cart = getCart(session);
    for (CartItemDTO existing : cart) {
      if (existing.getProductId().equals(item.getProductId())) {
        existing.setQuantity(existing.getQuantity() + item.getQuantity());
        return;
      }
    }
    cart.add(item);
  }

  public void removeItem(HttpSession session, Long productId) {
    List<CartItemDTO> cart = getCart(session);
    cart.removeIf(i -> i.getProductId().equals(productId));
  }

  public void updateQuantity(HttpSession session, Long productId, int quantity) {
    List<CartItemDTO> cart = getCart(session);
    if (quantity <= 0) {
      removeItem(session, productId);
      return;
    }
    for (CartItemDTO item : cart) {
      if (item.getProductId().equals(productId)) {
        item.setQuantity(quantity);
        return;
      }
    }
  }

  public void clearCart(HttpSession session) {
    session.removeAttribute(CART_KEY);
  }

  public int getTotalItems(HttpSession session) {
    return getCart(session).stream().mapToInt(CartItemDTO::getQuantity).sum();
  }

  public double getTotal(HttpSession session) {
    return getCart(session).stream().mapToDouble(CartItemDTO::getSubtotal).sum();
  }
}
