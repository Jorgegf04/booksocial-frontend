package com.example.booksocial_frontend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booksocial_frontend.dto.AuthorRequestDTO;
import com.example.booksocial_frontend.dto.AuthorResponseDTO;
import com.example.booksocial_frontend.dto.ChapterRequestDTO;
import com.example.booksocial_frontend.dto.ChapterResponseDTO;
import com.example.booksocial_frontend.dto.CommentResponseDTO;
import com.example.booksocial_frontend.dto.EditionRequestDTO;
import com.example.booksocial_frontend.dto.EditionResponseDTO;
import com.example.booksocial_frontend.dto.EditorialRequestDTO;
import com.example.booksocial_frontend.dto.EditorialResponseDTO;
import com.example.booksocial_frontend.dto.EventRequestDTO;
import com.example.booksocial_frontend.dto.EventResponseDTO;
import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;
import com.example.booksocial_frontend.dto.TomeRequestDTO;
import com.example.booksocial_frontend.dto.TomeResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.dto.VolumeRequestDTO;
import com.example.booksocial_frontend.dto.VolumeResponseDTO;
import com.example.booksocial_frontend.dto.WorkRequestDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.domain.Demographic;
import com.example.booksocial_frontend.domain.Genre;
import com.example.booksocial_frontend.domain.WorkType;
import com.example.booksocial_frontend.service.AuthorClientService;
import com.example.booksocial_frontend.service.ChapterClientService;
import com.example.booksocial_frontend.service.CommentClientService;
import com.example.booksocial_frontend.service.FileUploadClientService;
import com.example.booksocial_frontend.service.EditionClientService;
import com.example.booksocial_frontend.service.EditorialClientService;
import com.example.booksocial_frontend.service.EventClientService;
import com.example.booksocial_frontend.service.MailService;
import com.example.booksocial_frontend.service.OrderClientService;
import com.example.booksocial_frontend.service.ProductClientService;
import com.example.booksocial_frontend.service.TomeClientService;
import com.example.booksocial_frontend.service.TrackingOrderClientService;
import com.example.booksocial_frontend.service.UserClientService;
import com.example.booksocial_frontend.service.VolumeClientService;
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
  private final EditorialClientService editorialClientService;
  private final TomeClientService tomeClientService;
  private final ChapterClientService chapterClientService;
  private final VolumeClientService volumeClientService;
  private final CommentClientService commentClientService;
  private final FileUploadClientService fileUploadClientService;
  private final MailService mailService;
  private final TrackingOrderClientService trackingOrderClientService;

  // DASHBOARD

  @GetMapping({ "", "/", "/dashboard" })
  public String dashboard(Model model) {
    List<UserResponseDTO> users = List.of();
    try {
      users = userClientService.getAllUsers();
    } catch (Exception ignored) {
    }

    List<OrderResponseDTO> orders = List.of();
    try {
      orders = orderClientService.getAllOrders();
    } catch (Exception ignored) {
    }

    List<ProductResponseDTO> products = List.of();
    try {
      products = productClientService.getAvailableProducts();
    } catch (Exception ignored) {
    }

    List<EventResponseDTO> events = List.of();
    try {
      events = eventClientService.getUpcomingEvents();
    } catch (Exception ignored) {
    }

    List<WorkResponseDTO> works = List.of();
    try {
      works = workClientService.getAllWorks();
    } catch (Exception ignored) {
    }

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

  // WORKS

  @GetMapping("/works")
  public String works(Model model) {
    List<WorkResponseDTO> works = List.of();
    try {
      works = workClientService.getAllWorks();
    } catch (Exception ignored) {
    }
    List<AuthorResponseDTO> authors = List.of();
    try {
      authors = authorClientService.getAllAuthors();
    } catch (Exception ignored) {
    }
    model.addAttribute("works", works);
    model.addAttribute("authors", authors);
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
      @RequestParam(required = false) MultipartFile imgFile,
      @RequestParam(required = false) List<String> authors,
      RedirectAttributes ra) {

    try {
      WorkRequestDTO dto = new WorkRequestDTO();
      dto.setTitle(title);
      dto.setDescription(description);
      if (genre != null && !genre.isBlank())
        dto.setGenre(Genre.valueOf(genre));
      if (type != null && !type.isBlank())
        dto.setType(WorkType.valueOf(type));
      if (demographic != null && !demographic.isBlank())
        dto.setDemographic(Demographic.valueOf(demographic));
      if (publicationDate != null && !publicationDate.isBlank())
        dto.setPublicationDate(LocalDate.parse(publicationDate));
      dto.setImg(imgFile != null && !imgFile.isEmpty()
          ? fileUploadClientService.uploadImage(imgFile) : img);
      if (authors != null && !authors.isEmpty())
        dto.setAuthors(authors);
      WorkResponseDTO created = workClientService.createWork(dto);
      ra.addFlashAttribute("success", "Obra creada correctamente");

      if (authors != null && !authors.isEmpty()) {
        for (String authorIdStr : authors) {
          try {
            Long authorId = Long.parseLong(authorIdStr);
            AuthorResponseDTO author = authorClientService.getAuthorById(authorId);
            List<com.example.booksocial_frontend.dto.UserResponseDTO> followers =
                authorClientService.getFollowers(authorId);
            for (com.example.booksocial_frontend.dto.UserResponseDTO follower : followers) {
              mailService.sendNewWorkNotification(
                  follower.getEmail(), follower.getUsername() != null ? follower.getUsername() : follower.getName(),
                  created.getTitle(), author.getName());
            }
          } catch (Exception ignored) {}
        }
      }
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
      @RequestParam(required = false) MultipartFile imgFile,
      @RequestParam(required = false) List<String> authors,
      RedirectAttributes ra) {

    try {
      WorkRequestDTO dto = new WorkRequestDTO();
      dto.setTitle(title);
      dto.setDescription(description);
      if (genre != null && !genre.isBlank())
        dto.setGenre(Genre.valueOf(genre));
      if (type != null && !type.isBlank())
        dto.setType(WorkType.valueOf(type));
      if (demographic != null && !demographic.isBlank())
        dto.setDemographic(Demographic.valueOf(demographic));
      if (publicationDate != null && !publicationDate.isBlank())
        dto.setPublicationDate(LocalDate.parse(publicationDate));
      dto.setImg(imgFile != null && !imgFile.isEmpty()
          ? fileUploadClientService.uploadImage(imgFile) : img);
      if (authors != null && !authors.isEmpty())
        dto.setAuthors(authors);
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

  // AUTHORS

  @GetMapping("/authors")
  public String authors(Model model) {
    List<AuthorResponseDTO> authors = List.of();
    try {
      authors = authorClientService.getAllAuthors();
    } catch (Exception ignored) {
    }
    List<WorkResponseDTO> works = List.of();
    try {
      works = workClientService.getAllWorks();
    } catch (Exception ignored) {
    }
    model.addAttribute("authors", authors);
    model.addAttribute("works", works);
    return "admin/authors";
  }

  @PostMapping("/authors/create")
  public String createAuthor(
      @RequestParam String name,
      @RequestParam(required = false) String nationality,
      @RequestParam(required = false) String birthDate,
      @RequestParam(required = false) MultipartFile imgFile,
      @RequestParam(required = false) List<Long> workIds,
      RedirectAttributes ra) {

    try {
      AuthorRequestDTO dto = new AuthorRequestDTO();
      dto.setName(name);
      dto.setNationality(nationality);
      if (birthDate != null && !birthDate.isBlank())
        dto.setBirthDate(LocalDate.parse(birthDate));
      if (imgFile != null && !imgFile.isEmpty())
        dto.setImg(fileUploadClientService.uploadImage(imgFile));
      if (workIds != null && !workIds.isEmpty())
        dto.setWorkIds(workIds);
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
      @RequestParam(required = false) String img,
      @RequestParam(required = false) MultipartFile imgFile,
      @RequestParam(required = false) List<Long> workIds,
      RedirectAttributes ra) {

    try {
      AuthorRequestDTO dto = new AuthorRequestDTO();
      dto.setName(name);
      dto.setNationality(nationality);
      if (birthDate != null && !birthDate.isBlank())
        dto.setBirthDate(LocalDate.parse(birthDate));
      dto.setImg(imgFile != null && !imgFile.isEmpty()
          ? fileUploadClientService.uploadImage(imgFile) : img);
      dto.setWorkIds(workIds != null ? workIds : List.of());
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

  // EDITIONS

  @GetMapping("/editions")
  public String editions(Model model) {
    List<EditionResponseDTO> editions = List.of();
    try {
      editions = editionClientService.getAllEditions();
    } catch (Exception ignored) {
    }
    List<WorkResponseDTO> works = List.of();
    try {
      works = workClientService.getAllWorks();
    } catch (Exception ignored) {
    }
    List<EditorialResponseDTO> editorials = List.of();
    try {
      editorials = editorialClientService.getAllEditorials();
    } catch (Exception ignored) {
    }
    model.addAttribute("editions", editions);
    model.addAttribute("works", works);
    model.addAttribute("editorials", editorials);
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
      if (editionDate != null && !editionDate.isBlank())
        dto.setEditionDate(LocalDate.parse(editionDate));
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
      if (editionDate != null && !editionDate.isBlank())
        dto.setEditionDate(LocalDate.parse(editionDate));
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

  // EVENTS

  @GetMapping("/events")
  public String events(Model model) {
    List<EventResponseDTO> events = List.of();
    try {
      events = eventClientService.getAllEvents();
    } catch (Exception ignored) {
    }
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

  // USERS

  @GetMapping("/users")
  public String users(Model model) {
    List<UserResponseDTO> users = List.of();
    try {
      users = userClientService.getAllUsers();
    } catch (Exception ignored) {
    }
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
      @RequestParam(required = false) MultipartFile imgFile,
      RedirectAttributes ra) {

    try {
      String finalImg = imgFile != null && !imgFile.isEmpty()
          ? fileUploadClientService.uploadImage(imgFile) : img;
      UpdateUserRequestDTO dto = new UpdateUserRequestDTO(username, email, name, secondName, finalImg);
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

  // COMMENTS

  @GetMapping("/comments")
  public String comments(Model model) {
    List<CommentResponseDTO> comments = List.of();
    try {
      comments = commentClientService.getAllComments();
    } catch (Exception ignored) {
    }
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

  // EDITORIALS

  @GetMapping("/editorials")
  public String editorials(Model model) {
    List<EditorialResponseDTO> editorials = List.of();
    try {
      editorials = editorialClientService.getAllEditorials();
    } catch (Exception ignored) {
    }
    model.addAttribute("editorials", editorials);
    return "admin/editorials";
  }

  @PostMapping("/editorials/create")
  public String createEditorial(
      @RequestParam String name,
      @RequestParam(required = false) String country,
      RedirectAttributes ra) {

    try {
      EditorialRequestDTO dto = new EditorialRequestDTO();
      dto.setName(name);
      dto.setCountry(country);
      editorialClientService.createEditorial(dto);
      ra.addFlashAttribute("success", "Editorial creada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al crear la editorial: " + e.getMessage());
    }
    return "redirect:/admin/editorials";
  }

  @PostMapping("/editorials/{id}/update")
  public String updateEditorial(
      @PathVariable Long id,
      @RequestParam String name,
      @RequestParam(required = false) String country,
      RedirectAttributes ra) {

    try {
      EditorialRequestDTO dto = new EditorialRequestDTO();
      dto.setName(name);
      dto.setCountry(country);
      editorialClientService.updateEditorial(id, dto);
      ra.addFlashAttribute("success", "Editorial actualizada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar la editorial: " + e.getMessage());
    }
    return "redirect:/admin/editorials";
  }

  @PostMapping("/editorials/{id}/delete")
  public String deleteEditorial(@PathVariable Long id, RedirectAttributes ra) {
    try {
      editorialClientService.deleteEditorial(id);
      ra.addFlashAttribute("success", "Editorial eliminada correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar la editorial: " + e.getMessage());
    }
    return "redirect:/admin/editorials";
  }

  // TOMES

  @GetMapping("/tomes")
  public String tomes(Model model) {
    List<TomeResponseDTO> tomes = List.of();
    try {
      tomes = tomeClientService.getAllTomes();
    } catch (Exception ignored) {
    }
    List<EditionResponseDTO> editions = List.of();
    try {
      editions = editionClientService.getAllEditions();
    } catch (Exception ignored) {
    }
    model.addAttribute("tomes", tomes);
    model.addAttribute("editions", editions);
    return "admin/tomes";
  }

  @PostMapping("/tomes/create")
  public String createTome(
      @RequestParam Integer numberTome,
      @RequestParam(required = false) String title,
      @RequestParam Long editionId,
      RedirectAttributes ra) {

    try {
      TomeRequestDTO dto = new TomeRequestDTO();
      dto.setNumberTome(numberTome);
      dto.setTitle(title);
      dto.setEditionId(editionId);
      tomeClientService.createTome(dto);
      ra.addFlashAttribute("success", "Tomo creado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al crear el tomo: " + e.getMessage());
    }
    return "redirect:/admin/tomes";
  }

  @PostMapping("/tomes/{id}/update")
  public String updateTome(
      @PathVariable Long id,
      @RequestParam Integer numberTome,
      @RequestParam(required = false) String title,
      @RequestParam Long editionId,
      RedirectAttributes ra) {

    try {
      TomeRequestDTO dto = new TomeRequestDTO();
      dto.setNumberTome(numberTome);
      dto.setTitle(title);
      dto.setEditionId(editionId);
      tomeClientService.updateTome(id, dto);
      ra.addFlashAttribute("success", "Tomo actualizado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar el tomo: " + e.getMessage());
    }
    return "redirect:/admin/tomes";
  }

  @PostMapping("/tomes/{id}/delete")
  public String deleteTome(@PathVariable Long id, RedirectAttributes ra) {
    try {
      tomeClientService.deleteTome(id);
      ra.addFlashAttribute("success", "Tomo eliminado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar el tomo: " + e.getMessage());
    }
    return "redirect:/admin/tomes";
  }

  // CHAPTERS

  @GetMapping("/chapters")
  public String chapters(Model model) {
    List<ChapterResponseDTO> chapters = List.of();
    try {
      chapters = chapterClientService.getAllChapters();
    } catch (Exception ignored) {
    }
    List<TomeResponseDTO> tomes = List.of();
    try {
      tomes = tomeClientService.getAllTomes();
    } catch (Exception ignored) {
    }
    model.addAttribute("chapters", chapters);
    model.addAttribute("tomes", tomes);
    return "admin/chapters";
  }

  @PostMapping("/chapters/create")
  public String createChapter(
      @RequestParam Integer chapterNumber,
      @RequestParam(required = false) String title,
      @RequestParam Long tomeId,
      RedirectAttributes ra) {

    try {
      ChapterRequestDTO dto = new ChapterRequestDTO();
      dto.setChapterNumber(chapterNumber);
      dto.setTitle(title);
      dto.setTomeId(tomeId);
      chapterClientService.createChapter(dto);
      ra.addFlashAttribute("success", "Capítulo creado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al crear el capítulo: " + e.getMessage());
    }
    return "redirect:/admin/chapters";
  }

  @PostMapping("/chapters/{id}/update")
  public String updateChapter(
      @PathVariable Long id,
      @RequestParam Integer chapterNumber,
      @RequestParam(required = false) String title,
      @RequestParam Long tomeId,
      RedirectAttributes ra) {

    try {
      ChapterRequestDTO dto = new ChapterRequestDTO();
      dto.setChapterNumber(chapterNumber);
      dto.setTitle(title);
      dto.setTomeId(tomeId);
      chapterClientService.updateChapter(id, dto);
      ra.addFlashAttribute("success", "Capítulo actualizado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar el capítulo: " + e.getMessage());
    }
    return "redirect:/admin/chapters";
  }

  @PostMapping("/chapters/{id}/delete")
  public String deleteChapter(@PathVariable Long id, RedirectAttributes ra) {
    try {
      chapterClientService.deleteChapter(id);
      ra.addFlashAttribute("success", "Capítulo eliminado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar el capítulo: " + e.getMessage());
    }
    return "redirect:/admin/chapters";
  }

  // VOLUMES

  @GetMapping("/volumes")
  public String volumes(Model model) {
    List<VolumeResponseDTO> volumes = List.of();
    try {
      volumes = volumeClientService.getAllVolumes();
    } catch (Exception ignored) {
    }
    List<EditionResponseDTO> editions = List.of();
    try {
      editions = editionClientService.getAllEditions();
    } catch (Exception ignored) {
    }
    model.addAttribute("volumes", volumes);
    model.addAttribute("editions", editions);
    return "admin/volumes";
  }

  @PostMapping("/volumes/create")
  public String createVolume(
      @RequestParam Integer volumeNumber,
      @RequestParam(required = false) String title,
      @RequestParam Long editionId,
      RedirectAttributes ra) {

    try {
      VolumeRequestDTO dto = new VolumeRequestDTO();
      dto.setVolumeNumber(volumeNumber);
      dto.setTitle(title);
      dto.setEditionId(editionId);
      volumeClientService.createVolume(dto);
      ra.addFlashAttribute("success", "Volumen creado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al crear el volumen: " + e.getMessage());
    }
    return "redirect:/admin/volumes";
  }

  @PostMapping("/volumes/{id}/update")
  public String updateVolume(
      @PathVariable Long id,
      @RequestParam Integer volumeNumber,
      @RequestParam(required = false) String title,
      @RequestParam Long editionId,
      RedirectAttributes ra) {

    try {
      VolumeRequestDTO dto = new VolumeRequestDTO();
      dto.setVolumeNumber(volumeNumber);
      dto.setTitle(title);
      dto.setEditionId(editionId);
      volumeClientService.updateVolume(id, dto);
      ra.addFlashAttribute("success", "Volumen actualizado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar el volumen: " + e.getMessage());
    }
    return "redirect:/admin/volumes";
  }

  @PostMapping("/volumes/{id}/delete")
  public String deleteVolume(@PathVariable Long id, RedirectAttributes ra) {
    try {
      volumeClientService.deleteVolume(id);
      ra.addFlashAttribute("success", "Volumen eliminado correctamente");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al eliminar el volumen: " + e.getMessage());
    }
    return "redirect:/admin/volumes";
  }

  // ORDERS — tracking de estado con notificación por email

  @PostMapping("/orders/{orderId}/tracking")
  public String updateOrderTracking(
      @PathVariable Long orderId,
      @RequestParam String status,
      RedirectAttributes ra) {

    try {
      var tracking = trackingOrderClientService.createTracking(orderId, status);

      String statusLabel = tracking != null && tracking.getStatusLabel() != null
          ? tracking.getStatusLabel() : status;

      try {
        var order = orderClientService.getAllOrders().stream()
            .filter(o -> orderId.equals(o.getId()))
            .findFirst().orElse(null);
        if (order != null && order.getUserId() != null) {
          var user = userClientService.getUserById(order.getUserId());
          String username = order.getUsername() != null ? order.getUsername() : user.getName();
          mailService.sendOrderStatusUpdate(user.getEmail(), username, orderId, statusLabel);
        }
      } catch (Exception ignored) {}

      ra.addFlashAttribute("success", "Estado del pedido #" + orderId + " actualizado a: " + statusLabel);
    } catch (Exception e) {
      ra.addFlashAttribute("error", "Error al actualizar el estado: " + e.getMessage());
    }
    return "redirect:/admin/orders";
  }

  // COMMERCE (sin CRUD)

  @GetMapping("/orders")
  public String orders(Model model) {
    List<OrderResponseDTO> orders = List.of();
    try {
      orders = orderClientService.getAllOrders();
    } catch (Exception ignored) {
    }
    model.addAttribute("orders", orders);
    return "admin/orders";
  }

  @GetMapping("/inventory")
  public String inventory(Model model) {
    List<ProductResponseDTO> products = List.of();
    try {
      products = productClientService.getAvailableProducts();
    } catch (Exception ignored) {
    }
    model.addAttribute("products", products);
    return "admin/inventory";
  }
}
