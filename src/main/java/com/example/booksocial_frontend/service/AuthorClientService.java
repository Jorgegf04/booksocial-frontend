package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.AuthorRequestDTO;
import com.example.booksocial_frontend.dto.AuthorResponseDTO;

@Service
public class AuthorClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/authors");

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
}
