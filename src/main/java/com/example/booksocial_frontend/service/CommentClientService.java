package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.CommentResponseDTO;

@Service
public class CommentClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/comments");

  public List<CommentResponseDTO> getRootCommentsByWork(Long workId) {
    return restClient.get()
        .uri("/work/{workId}/root", workId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<CommentResponseDTO>>() {});
  }

  public List<CommentResponseDTO> getAllComments() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<CommentResponseDTO>>() {});
  }

  public void deleteComment(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}
