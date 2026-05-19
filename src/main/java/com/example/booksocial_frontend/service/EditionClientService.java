package com.example.booksocial_frontend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.EditionRequestDTO;
import com.example.booksocial_frontend.dto.EditionResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

@Service
public class EditionClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  @Autowired
  private SessionJwtInterceptor jwtInterceptor;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.builder()
        .baseUrl(apiBaseUrl + "/editions")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  public List<EditionResponseDTO> getAllEditions() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<EditionResponseDTO>>() {});
  }

  public List<EditionResponseDTO> getEditionsByWork(Long workId) {
    return getAllEditions().stream()
        .filter(e -> workId.equals(e.getWorkId()))
        .collect(Collectors.toList());
  }

  public EditionResponseDTO createEdition(EditionRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EditionResponseDTO.class);
  }

  public EditionResponseDTO updateEdition(Long id, EditionRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EditionResponseDTO.class);
  }

  public void deleteEdition(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}
