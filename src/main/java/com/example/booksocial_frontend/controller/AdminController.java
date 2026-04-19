package com.example.booksocial_frontend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booksocial_frontend.dto.AuthorRequestDTO;
import com.example.booksocial_frontend.dto.AuthorResponseDTO;
import com.example.booksocial_frontend.dto.CommentResponseDTO;
import com.example.booksocial_frontend.dto.EditionRequestDTO;
import com.example.booksocial_frontend.dto.EditionResponseDTO;
import com.example.booksocial_frontend.dto.EventRequestDTO;
import com.example.booksocial_frontend.dto.EventResponseDTO;
import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.dto.WorkRequestDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.domain.Demographic;
import com.example.booksocial_frontend.domain.Genre;
import com.example.booksocial_frontend.domain.WorkType;
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

  // ─── DASHBOARD ───────────────────────────────────────────────────────────────

  @GetMapping({ "", "/", "/dashboard" })
  public String dashboard(Model model) {
    List<UserResponseDTO> users = List.of();
    try { users = userClientService.getAllUsers(); } catch (Exception ignored) {}

    List<OrderResponseDTO> orders = List.of();
    try { orders = orderClientService.getAllOrders(); } catch (Exception ignored) {}

    List<ProductResponseDTO> products = List.of();
    try { products = productClientService.getAvailableProducts(); } catch (Exception ignored) {}

    List<EventResponseDTO> events = List.of();
    try { events = eventClientService.getUpcomingEvents(); } catch (Exception ignored) {}

    List<WorkResponseDTO> works = List.of();
    try { works = workClientService.getAllWorks(); } catch (Exception ignored) {}

    long totalUsers = users.size();
    double totalRevenue = orders.stream().mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0).sum();
    long lowStock = products.stream().filter(p -> p.getStock() != null && p.getStock() <= 5).count();
    long pendingOrders = orders.size();

    int currentMonth = java.time.LocalDate.now().getMonthValue();
    int currentYear = java.time.LocalDate.now().getYear();
    long newMembers = users.stream()
        .filter(u -> u.getRegistrationDate() != null
            && u.getRegistrationDate().getMonthValue() == currentMonth
            && u.getRegistrationDate().getYear() == currentYear)
        .count();

    model.addAttribute("totalUsers", totalUsers);
    model.addAttribute("totalRevenue", totalRevenue);
    model.addAttribute("lowStockCount", lowStock);
    model.addAttribute("pendingOrders", pendingOrders);
    model.addAttribute("newMembers", newMembers);
    model.addAttribute("users", users);
    model.addAttribute("orders", orders);
    model.addAttribute("products", products);
    model.addAttribute("events", events.stream().limit(4).toList());
    model.addAttribute("works", works.stream().limit(10).toList());
    model.addAttribute("lowStockProducts",
        products.stream().filter(p -> p.getStock() != null && p.getStock() <= 5).limit(5).toList());

    return "admin/dashboard";
  }

  // ─── WORKS ───────────────────────────────────────────────────────────────────

  @GetMapping("/works")
  public String works(Model model) {
    List<WorkResponseDTO> works = List.of();
    try { works = workClientService.getAllWorks(); } catch (Exception ignored) {}
    model.addAttribute("works", works);
    model.addAttribute("genres", Genre.values());
    model.addAttribute("types", WorkType.values());
    model.addAttribute("demographics", Demographic.values());
    return "admin/works";
  }

  @PostMapping("/works/create")
  public String createWork(
      @RequestParam String title,
      @RequestParam(required = false) String description,
      @RequestParam(required = false) String genre,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String demographic,
      @RequestParam(required = false) String publicationDate,
      @RequestParam(required = false) String img,
      @RequestParam(required = false) String authors,
      RedirectAttributes ra) {

    try {
      WorkRequestDTO dto = new WorkRequestDTO();
      dto.setTitle(title);
      dto.setDescription(description);
      if (genre != null && !genre.isBlank()) dto.setGenre(Genre.valueOf(genre));
      if (type != null && !type.isBlank()) dto.setType(WorkType.valueOf(type));
      if (demographic != null && !demographic.isBlank()) dto.setDemographic(Demographic.valueOf(demographic));
      if (publicationDate != null && !publicationDate.isBlank()) dto.setPublicationDate(LocalDate.parse(publicationDate));
      dto.setImg(img);
      if (authors != null && !authors.isBlank()) {
        dto.setAuthors(Arrays.stream(authors.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
      }
      workClientService.createWork(dto);
      ra.addFlashAttribute("success", "Obra creada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al crear la obra: " + e.getMessage());
    }
    return "redirect:/admin/works";
  }

  @PostMapping("/works/{id}/update")
  public String updateWork(
      @PathVariable Long id,
      @RequestParam String title,
      @RequestParam(required = false) String description,
      @RequestParam(required = false) String genre,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String demographic,
      @RequestParam(required = false) String publicationDate,
      @RequestParam(required = false) String img,
      @RequestParam(required = false) String authors,
      RedirectAttributes ra) {

    try {
      WorkRequestDTO dto = new WorkRequestDTO();
      dto.setTitle(title);
      dto.setDescription(description);
      if (genre != null && !genre.isBlank()) dto.setGenre(Genre.valueOf(genre));
      if (type != null && !type.isBlank()) dto.setType(WorkType.valueOf(type));
      if (demographic != null && !demographic.isBlank()) dto.setDemographic(Demographic.valueOf(demographic));
      if (publicationDate != null && !publicationDate.isBlank()) dto.setPublicationDate(LocalDate.parse(publicationDate));
      dto.setImg(img);
      if (authors != null && !authors.isBlank()) {
        dto.setAuthors(Arrays.stream(authors.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
      }
      workClientService.updateWork(id, dto);
      ra.addFlashAttribute("success", "Obra actualizada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar la obra: " + e.getMessage());
    }
    return "redirect:/admin/works";
  }

  @PostMapping("/works/{id}/delete")
  public String deleteWork(@PathVariable Long id, RedirectAttributes ra) {
    try {
      workClientService.deleteWork(id);
      ra.addFlashAttribute("success", "Obra eliminada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar la obra: " + e.getMessage());
    }
    return "redirect:/admin/works";
  }

  // ─── AUTHORS ─────────────────────────────────────────────────────────────────

  @GetMapping("/authors")
  public String authors(Model model) {
    List<AuthorResponseDTO> authors = List.of();
    try { authors = authorClientService.getAllAuthors(); } catch (Exception ignored) {}
    model.addAttribute("authors", authors);
    return "admin/authors";
  }

  @PostMapping("/authors/create")
  public String createAuthor(
      @RequestParam String name,
      @RequestParam(required = false) String nationality,
      @RequestParam(required = false) String birthDate,
      RedirectAttributes ra) {

    try {
      AuthorRequestDTO dto = new AuthorRequestDTO();
      dto.setName(name);
      dto.setNationality(nationality);
      if (birthDate != null && !birthDate.isBlank()) dto.setBirthDate(LocalDate.parse(birthDate));
      authorClientService.createAuthor(dto);
      ra.addFlashAttribute("success", "Autor creado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al crear el autor: " + e.getMessage());
    }
    return "redirect:/admin/authors";
  }

  @PostMapping("/authors/{id}/update")
  public String updateAuthor(
      @PathVariable Long id,
      @RequestParam String name,
      @RequestParam(required = false) String nationality,
      @RequestParam(required = false) String birthDate,
      RedirectAttributes ra) {

    try {
      AuthorRequestDTO dto = new AuthorRequestDTO();
      dto.setName(name);
      dto.setNationality(nationality);
      if (birthDate != null && !birthDate.isBlank()) dto.setBirthDate(LocalDate.parse(birthDate));
      authorClientService.updateAuthor(id, dto);
      ra.addFlashAttribute("success", "Autor actualizado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar el autor: " + e.getMessage());
    }
    return "redirect:/admin/authors";
  }

  @PostMapping("/authors/{id}/delete")
  public String deleteAuthor(@PathVariable Long id, RedirectAttributes ra) {
    try {
      authorClientService.deleteAuthor(id);
      ra.addFlashAttribute("success", "Autor eliminado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar el autor: " + e.getMessage());
    }
    return "redirect:/admin/authors";
  }

  // ─── EDITIONS ────────────────────────────────────────────────────────────────

  @GetMapping("/editions")
  public String editions(Model model) {
    List<EditionResponseDTO> editions = List.of();
    try { editions = editionClientService.getAllEditions(); } catch (Exception ignored) {}
    List<WorkResponseDTO> works = List.of();
    try { works = workClientService.getAllWorks(); } catch (Exception ignored) {}
    model.addAttribute("editions", editions);
    model.addAttribute("works", works);
    return "admin/editions";
  }

  @PostMapping("/editions/create")
  public String createEdition(
      @RequestParam(required = false) String isbn,
      @RequestParam(required = false) String editionDate,
      @RequestParam(required = false) Long workId,
      @RequestParam(required = false) Long editorialId,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Integer totalTomes,
      RedirectAttributes ra) {

    try {
      EditionRequestDTO dto = new EditionRequestDTO();
      dto.setIsbn(isbn);
      if (editionDate != null && !editionDate.isBlank()) dto.setEditionDate(LocalDate.parse(editionDate));
      dto.setWorkId(workId);
      dto.setEditorialId(editorialId);
      dto.setTitle(title);
      dto.setTotalTomes(totalTomes);
      editionClientService.createEdition(dto);
      ra.addFlashAttribute("success", "Edición creada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al crear la edición: " + e.getMessage());
    }
    return "redirect:/admin/editions";
  }

  @PostMapping("/editions/{id}/update")
  public String updateEdition(
      @PathVariable Long id,
      @RequestParam(required = false) String isbn,
      @RequestParam(required = false) String editionDate,
      @RequestParam(required = false) Long workId,
      @RequestParam(required = false) Long editorialId,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Integer totalTomes,
      RedirectAttributes ra) {

    try {
      EditionRequestDTO dto = new EditionRequestDTO();
      dto.setIsbn(isbn);
      if (editionDate != null && !editionDate.isBlank()) dto.setEditionDate(LocalDate.parse(editionDate));
      dto.setWorkId(workId);
      dto.setEditorialId(editorialId);
      dto.setTitle(title);
      dto.setTotalTomes(totalTomes);
      editionClientService.updateEdition(id, dto);
      ra.addFlashAttribute("success", "Edición actualizada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar la edición: " + e.getMessage());
    }
    return "redirect:/admin/editions";
  }

  @PostMapping("/editions/{id}/delete")
  public String deleteEdition(@PathVariable Long id, RedirectAttributes ra) {
    try {
      editionClientService.deleteEdition(id);
      ra.addFlashAttribute("success", "Edición eliminada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar la edición: " + e.getMessage());
    }
    return "redirect:/admin/editions";
  }

  // ─── EVENTS ──────────────────────────────────────────────────────────────────

  @GetMapping("/events")
  public String events(Model model) {
    List<EventResponseDTO> events = List.of();
    try { events = eventClientService.getAllEvents(); } catch (Exception ignored) {}
    model.addAttribute("events", events);
    return "admin/events";
  }

  @PostMapping("/events/create")
  public String createEvent(
      @RequestParam String title,
      @RequestParam(required = false) String description,
      @RequestParam String date,
      RedirectAttributes ra) {

    try {
      EventRequestDTO dto = new EventRequestDTO();
      dto.setTitle(title);
      dto.setDescription(description);
      dto.setDate(LocalDateTime.parse(date));
      eventClientService.createEvent(dto);
      ra.addFlashAttribute("success", "Evento creado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al crear el evento: " + e.getMessage());
    }
    return "redirect:/admin/events";
  }

  @PostMapping("/events/{id}/update")
  public String updateEvent(
      @PathVariable Long id,
      @RequestParam String title,
      @RequestParam(required = false) String description,
      @RequestParam String date,
      RedirectAttributes ra) {

    try {
      EventRequestDTO dto = new EventRequestDTO();
      dto.setTitle(title);
      dto.setDescription(description);
      dto.setDate(LocalDateTime.parse(date));
      eventClientService.updateEvent(id, dto);
      ra.addFlashAttribute("success", "Evento actualizado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar el evento: " + e.getMessage());
    }
    return "redirect:/admin/events";
  }

  @PostMapping("/events/{id}/delete")
  public String deleteEvent(@PathVariable Long id, RedirectAttributes ra) {
    try {
      eventClientService.deleteEvent(id);
      ra.addFlashAttribute("success", "Evento eliminado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar el evento: " + e.getMessage());
    }
    return "redirect:/admin/events";
  }

  // ─── USERS ───────────────────────────────────────────────────────────────────

  @GetMapping("/users")
  public String users(Model model) {
    List<UserResponseDTO> users = List.of();
    try { users = userClientService.getAllUsers(); } catch (Exception ignored) {}
    model.addAttribute("users", users);
    return "admin/users";
  }

  @PostMapping("/users/{id}/update")
  public String updateUser(
      @PathVariable Long id,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String secondName,
      @RequestParam(required = false) String img,
      RedirectAttributes ra) {

    try {
      UpdateUserRequestDTO dto = new UpdateUserRequestDTO(username, email, name, secondName, img);
      userClientService.updateUser(id, dto);
      ra.addFlashAttribute("success", "Usuario actualizado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
    }
    return "redirect:/admin/users";
  }

  @PostMapping("/users/{id}/delete")
  public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
    try {
      userClientService.deleteUser(id);
      ra.addFlashAttribute("success", "Usuario eliminado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
    }
    return "redirect:/admin/users";
  }

  // ─── COMMENTS ────────────────────────────────────────────────────────────────

  @GetMapping("/comments")
  public String comments(Model model) {
    List<CommentResponseDTO> comments = List.of();
    try { comments = commentClientService.getAllComments(); } catch (Exception ignored) {}
    model.addAttribute("comments", comments);
    return "admin/comments";
  }

  @PostMapping("/comments/{id}/delete")
  public String deleteComment(@PathVariable Long id, RedirectAttributes ra) {
    try {
      commentClientService.deleteComment(id);
      ra.addFlashAttribute("success", "Comentario eliminado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar el comentario: " + e.getMessage());
    }
    return "redirect:/admin/comments";
  }

  // ─── COMMERCE (sin CRUD) ─────────────────────────────────────────────────────

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
}
