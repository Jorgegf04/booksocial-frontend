package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.WorkRequestDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/works");

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
