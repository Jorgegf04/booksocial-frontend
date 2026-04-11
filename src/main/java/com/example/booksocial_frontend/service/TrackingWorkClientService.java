package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.TrackingWorkRequestDTO;
import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;

@Service
public class TrackingWorkClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/tracking-works");

  public List<TrackingWorkResponseDTO> getByUser(Long userId) {
    return restClient.get()
        .uri("/user/{userId}", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<TrackingWorkResponseDTO>>() {});
  }

  public TrackingWorkResponseDTO create(TrackingWorkRequestDTO request) {
    return restClient.post()
        .uri("")
        .body(request)
        .retrieve()
        .body(TrackingWorkResponseDTO.class);
  }

  public TrackingWorkResponseDTO update(Long trackingId, TrackingWorkRequestDTO request) {
    return restClient.put()
        .uri("/{id}", trackingId)
        .body(request)
        .retrieve()
        .body(TrackingWorkResponseDTO.class);
  }

  public void delete(Long trackingId) {
    restClient.delete()
        .uri("/{id}", trackingId)
        .retrieve()
        .toBodilessEntity();
  }
}
