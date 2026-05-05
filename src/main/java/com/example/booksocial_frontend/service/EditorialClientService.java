package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.EditorialRequestDTO;
import com.example.booksocial_frontend.dto.EditorialResponseDTO;

import jakarta.annotation.PostConstruct;

@Service
public class EditorialClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.create(apiBaseUrl + "/editorials");
  }

  public List<EditorialResponseDTO> getAllEditorials() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<EditorialResponseDTO>>() {});
  }

  public EditorialResponseDTO createEditorial(EditorialRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EditorialResponseDTO.class);
  }

  public EditorialResponseDTO updateEditorial(Long id, EditorialRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EditorialResponseDTO.class);
  }

  public void deleteEditorial(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}
