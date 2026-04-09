package com.example.booksocial_frontend.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booksocial_frontend.dto.CommentRequestDTO;
import com.example.booksocial_frontend.dto.CommentResponseDTO;
import com.example.booksocial_frontend.dto.EditionResponseDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;
import com.example.booksocial_frontend.dto.ReactionResponseDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.service.CommentClientService;
import com.example.booksocial_frontend.service.EditionClientService;
import com.example.booksocial_frontend.service.ProductClientService;
import com.example.booksocial_frontend.service.ReactionClientService;
import com.example.booksocial_frontend.service.WorkClientService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/work")
public class WorkDetailController {

  private final WorkClientService workService;
  private final EditionClientService editionService;
  private final CommentClientService commentService;
  private final ProductClientService productService;
  private final ReactionClientService reactionService;

  @GetMapping("/{id}")
  public String showWork(@PathVariable Long id, HttpSession session, Model model) {

    WorkResponseDTO work = workService.getWorkById(id);
    List<EditionResponseDTO> editions = editionService.getEditionsByWork(id);
    List<CommentResponseDTO> comments = commentService.getRootCommentsByWork(id);

    // Ordenar todo el árbol de más reciente a más antiguo
    sortRecursive(comments);

    // Session user info
    Long userId = (Long) session.getAttribute("userId");
    String role = (String) session.getAttribute("role");
    boolean loggedIn = userId != null;

    // Producto disponible para comprar
    ProductResponseDTO buyProduct = null;
    try {
      List<ProductResponseDTO> products = productService.getProductsByWork(id);
      buyProduct = products.stream()
          .filter(p -> p.getStock() != null && p.getStock() > 0)
          .findFirst()
          .orElse(null);
    } catch (Exception ignored) {}

    // Contar total de comentarios (árbol completo)
    int totalCommentCount = countComments(comments);

    // Recopilar todos los comentarios del árbol (para cargar reacciones)
    List<CommentResponseDTO> allComments = new ArrayList<>();
    collectComments(comments, allComments);

    // Cargar conteo de reacciones y likes del usuario por comentario
    Map<Long, Integer> reactionCounts = new HashMap<>();
    Set<Long> userLikedCommentIds = new HashSet<>();

    for (CommentResponseDTO comment : allComments) {
      try {
        reactionCounts.put(comment.getId(), reactionService.getReactionCount(comment.getId()));
      } catch (Exception e) {
        reactionCounts.put(comment.getId(), 0);
      }
      if (loggedIn) {
        try {
          List<ReactionResponseDTO> reactions = reactionService.getReactionsByComment(comment.getId());
          boolean liked = reactions.stream()
              .anyMatch(r -> userId.equals(r.getUserId()) && Boolean.TRUE.equals(r.getLiked()));
          if (liked) userLikedCommentIds.add(comment.getId());
        } catch (Exception ignored) {}
      }
    }

    model.addAttribute("work", work);
    model.addAttribute("editions", editions);
    model.addAttribute("comments", comments);
    model.addAttribute("commentCount", totalCommentCount);
    model.addAttribute("buyProduct", buyProduct);
    model.addAttribute("loggedIn", loggedIn);
    model.addAttribute("sessionUserId", userId);
    model.addAttribute("sessionRole", role);
    model.addAttribute("reactionCounts", reactionCounts);
    model.addAttribute("userLikedCommentIds", userLikedCommentIds);

    return "work/detail";
  }

  // ── POST: Publicar comentario raíz ──────────────────────────────────────────
  @PostMapping("/{workId}/comment")
  public String addComment(@PathVariable Long workId,
                           @RequestParam String content,
                           HttpSession session,
                           RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      commentService.createComment(new CommentRequestDTO(content, userId, workId, null));
    } catch (Exception e) {
      ra.addFlashAttribute("commentError", "No se pudo publicar el comentario.");
    }
    return "redirect:/work/" + workId + "#comments";
  }

  // ── POST: Responder a un comentario ─────────────────────────────────────────
  @PostMapping("/{workId}/comment/{parentId}/reply")
  public String addReply(@PathVariable Long workId,
                         @PathVariable Long parentId,
                         @RequestParam String content,
                         HttpSession session,
                         RedirectAttributes ra) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      commentService.replyToComment(parentId, new CommentRequestDTO(content, userId, workId, parentId));
    } catch (Exception e) {
      ra.addFlashAttribute("commentError", "No se pudo publicar la respuesta.");
    }
    return "redirect:/work/" + workId + "#comments";
  }

  // ── POST: Eliminar comentario ────────────────────────────────────────────────
  @PostMapping("/{workId}/comment/{commentId}/delete")
  public String deleteComment(@PathVariable Long workId,
                              @PathVariable Long commentId,
                              HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      commentService.deleteComment(commentId);
    } catch (Exception ignored) {}
    return "redirect:/work/" + workId + "#comments";
  }

  // ── Helpers recursivos ──────────────────────────────────────────────────────

  private int countComments(List<CommentResponseDTO> comments) {
    if (comments == null) return 0;
    int count = 0;
    for (CommentResponseDTO c : comments) {
      count++;
      count += countComments(c.getReplies());
    }
    return count;
  }

  private void collectComments(List<CommentResponseDTO> comments, List<CommentResponseDTO> result) {
    if (comments == null) return;
    for (CommentResponseDTO c : comments) {
      result.add(c);
      collectComments(c.getReplies(), result);
    }
  }

  private void sortRecursive(List<CommentResponseDTO> comments) {
    if (comments == null || comments.isEmpty()) return;
    comments.sort(Comparator.comparing(CommentResponseDTO::getDate,
        Comparator.nullsLast(Comparator.reverseOrder())));
    for (CommentResponseDTO c : comments) {
      sortRecursive(c.getReplies());
    }
  }

  // ── POST: Toggle reacción (like/unlike) ─────────────────────────────────────
  @PostMapping("/{workId}/comment/{commentId}/reaction")
  public String toggleReaction(@PathVariable Long workId,
                               @PathVariable Long commentId,
                               HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/auth/login";

    try {
      reactionService.toggleReaction(userId, commentId);
    } catch (Exception ignored) {}
    return "redirect:/work/" + workId + "#comments";
  }
}
