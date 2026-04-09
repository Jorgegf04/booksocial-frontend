package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.ReactionRequestDTO;
import com.example.booksocial_frontend.dto.ReactionResponseDTO;

@Service
public class ReactionClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/reactions");

  public ReactionResponseDTO toggleReaction(Long userId, Long commentId) {
    ReactionRequestDTO req = new ReactionRequestDTO(userId, commentId);
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(req)
        .retrieve()
        .body(ReactionResponseDTO.class);
  }

  public List<ReactionResponseDTO> getReactionsByComment(Long commentId) {
    return restClient.get()
        .uri("/comment/{commentId}", commentId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<ReactionResponseDTO>>() {});
  }

  public int getReactionCount(Long commentId) {
    Integer count = restClient.get()
        .uri("/comment/{commentId}/count", commentId)
        .retrieve()
        .body(Integer.class);
    return count != null ? count : 0;
  }
}
