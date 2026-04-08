package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
}
