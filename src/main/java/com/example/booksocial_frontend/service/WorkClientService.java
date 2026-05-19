package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.WorkRequestDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

@Service
public class WorkClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  @Autowired
  private SessionJwtInterceptor jwtInterceptor;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.builder()
        .baseUrl(apiBaseUrl + "/works")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  public List<WorkResponseDTO> getAllWorks() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<WorkResponseDTO>>() {});
  }

  public WorkResponseDTO getWorkById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(WorkResponseDTO.class);
  }

  public List<WorkResponseDTO> search(String title, String genre, Double rating) {
    return restClient.get()
        .uri(uriBuilder -> {
          var builder = uriBuilder.path("/search");
          if (title != null && !title.isBlank()) builder = builder.queryParam("title", title);
          if (genre != null) builder = builder.queryParam("genre", genre);
          if (rating != null && rating > 0) builder = builder.queryParam("rating", rating);
          return builder.build();
        })
        .retrieve()
        .body(new ParameterizedTypeReference<List<WorkResponseDTO>>() {});
  }

  public WorkResponseDTO createWork(WorkRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(WorkResponseDTO.class);
  }

  public WorkResponseDTO updateWork(Long id, WorkRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(WorkResponseDTO.class);
  }

  public void deleteWork(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}
