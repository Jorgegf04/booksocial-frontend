package com.example.booksocial_frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.booksocial_frontend.dto.AuthorResponseDTO;
import com.example.booksocial_frontend.dto.CommentResponseDTO;
import com.example.booksocial_frontend.dto.EditionResponseDTO;
import com.example.booksocial_frontend.dto.EventResponseDTO;
import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.service.AuthorClientService;
import com.example.booksocial_frontend.service.CommentClientService;
import com.example.booksocial_frontend.service.EditionClientService;
import com.example.booksocial_frontend.service.EventClientService;
import com.example.booksocial_frontend.service.OrderClientService;
import com.example.booksocial_frontend.service.ProductClientService;
import com.example.booksocial_frontend.service.UserClientService;
import com.example.booksocial_frontend.service.WorkClientService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserClientService userClientService;
  private final OrderClientService orderClientService;
  private final ProductClientService productClientService;
  private final EventClientService eventClientService;
  private final WorkClientService workClientService;
  private final AuthorClientService authorClientService;
  private final EditionClientService editionClientService;
  private final CommentClientService commentClientService;

  @GetMapping({ "", "/", "/dashboard" })
  public String dashboard(Model model) {

    // Users
    List<UserResponseDTO> users = List.of();
    try { users = userClientService.getAllUsers(); } catch (Exception ignored) {}

    // Orders
    List<OrderResponseDTO> orders = List.of();
    try { orders = orderClientService.getAllOrders(); } catch (Exception ignored) {}

    // Products (for low-stock alerts)
    List<ProductResponseDTO> products = List.of();
    try { products = productClientService.getAvailableProducts(); } catch (Exception ignored) {}

    // Upcoming events
    List<EventResponseDTO> events = List.of();
    try { events = eventClientService.getUpcomingEvents(); } catch (Exception ignored) {}

    // Works for the table
    List<WorkResponseDTO> works = List.of();
    try { works = workClientService.getAllWorks(); } catch (Exception ignored) {}

    // Metrics
    long totalUsers     = users.size();
    double totalRevenue = orders.stream().mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0).sum();
    long lowStock       = products.stream().filter(p -> p.getStock() != null && p.getStock() <= 5).count();
    long pendingOrders  = orders.size(); // all orders treated as pending for display

    // New members this month (registration date in current month)
    int currentMonth = java.time.LocalDate.now().getMonthValue();
    int currentYear  = java.time.LocalDate.now().getYear();
    long newMembers  = users.stream()
        .filter(u -> u.getRegistrationDate() != null
            && u.getRegistrationDate().getMonthValue() == currentMonth
            && u.getRegistrationDate().getYear() == currentYear)
        .count();

    model.addAttribute("totalUsers",     totalUsers);
    model.addAttribute("totalRevenue",   totalRevenue);
    model.addAttribute("lowStockCount",  lowStock);
    model.addAttribute("pendingOrders",  pendingOrders);
    model.addAttribute("newMembers",     newMembers);

    model.addAttribute("users",    users);
    model.addAttribute("orders",   orders);
    model.addAttribute("products", products);
    model.addAttribute("events",   events.stream().limit(4).toList());
    model.addAttribute("works",    works.stream().limit(10).toList());

    // Low-stock products (stock <= 5) for alert panel
    model.addAttribute("lowStockProducts",
        products.stream().filter(p -> p.getStock() != null && p.getStock() <= 5).limit(5).toList());

    return "admin/dashboard";
  }

  @GetMapping("/users")
  public String users(Model model) {
    List<UserResponseDTO> users = List.of();
    try { users = userClientService.getAllUsers(); } catch (Exception ignored) {}
    model.addAttribute("users", users);
    return "admin/users";
  }

  @GetMapping("/orders")
  public String orders(Model model) {
    List<OrderResponseDTO> orders = List.of();
    try { orders = orderClientService.getAllOrders(); } catch (Exception ignored) {}
    model.addAttribute("orders", orders);
    return "admin/orders";
  }

  @GetMapping("/inventory")
  public String inventory(Model model) {
    List<ProductResponseDTO> products = List.of();
    try { products = productClientService.getAvailableProducts(); } catch (Exception ignored) {}
    model.addAttribute("products", products);
    return "admin/inventory";
  }

  @GetMapping("/works")
  public String works(Model model) {
    List<WorkResponseDTO> works = List.of();
    try { works = workClientService.getAllWorks(); } catch (Exception ignored) {}
    model.addAttribute("works", works);
    return "admin/works";
  }

  @GetMapping("/authors")
  public String authors(Model model) {
    List<AuthorResponseDTO> authors = List.of();
    try { authors = authorClientService.getAllAuthors(); } catch (Exception ignored) {}
    model.addAttribute("authors", authors);
    return "admin/authors";
  }

  @GetMapping("/editions")
  public String editions(Model model) {
    List<EditionResponseDTO> editions = List.of();
    try { editions = editionClientService.getAllEditions(); } catch (Exception ignored) {}
    model.addAttribute("editions", editions);
    return "admin/editions";
  }

  @GetMapping("/comments")
  public String comments(Model model) {
    List<CommentResponseDTO> comments = List.of();
    try { comments = commentClientService.getAllComments(); } catch (Exception ignored) {}
    model.addAttribute("comments", comments);
    return "admin/comments";
  }

  @GetMapping("/events")
  public String events(Model model) {
    List<EventResponseDTO> events = List.of();
    try { events = eventClientService.getAllEvents(); } catch (Exception ignored) {}
    model.addAttribute("events", events);
    return "admin/events";
  }
}
