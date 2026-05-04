package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.AuthorRequestDTO;
import com.example.booksocial_frontend.dto.AuthorResponseDTO;

import jakarta.annotation.PostConstruct;

@Service
public class AuthorClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.create(apiBaseUrl + "/authors");
  }

  public List<AuthorResponseDTO> getAllAuthors() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<AuthorResponseDTO>>() {});
  }

  public AuthorResponseDTO getAuthorById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(AuthorResponseDTO.class);
  }

  public AuthorResponseDTO createAuthor(AuthorRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(AuthorResponseDTO.class);
  }

  public AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(AuthorResponseDTO.class);
  }

  public void deleteAuthor(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }

  public void followAuthor(Long authorId, Long userId) {
    restClient.post()
        .uri("/{id}/follow?userId={userId}", authorId, userId)
        .retrieve()
        .toBodilessEntity();
  }

  public void unfollowAuthor(Long authorId, Long userId) {
    restClient.delete()
        .uri("/{id}/follow?userId={userId}", authorId, userId)
        .retrieve()
        .toBodilessEntity();
  }

  public boolean isFollowing(Long authorId, Long userId) {
    try {
      Boolean result = restClient.get()
          .uri("/{id}/following?userId={userId}", authorId, userId)
          .retrieve()
          .body(Boolean.class);
      return Boolean.TRUE.equals(result);
    } catch (Exception e) {
      return false;
    }
  }
}
