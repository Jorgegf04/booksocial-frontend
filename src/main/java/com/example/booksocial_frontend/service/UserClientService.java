package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;

@Service
public class UserClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/users");

  public List<UserResponseDTO> getAllUsers() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<UserResponseDTO>>() {});
  }

  public UserResponseDTO getUserById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(UserResponseDTO.class);
  }

  public List<TrackingWorkResponseDTO> getTrackingByUser(Long userId) {
    return restClient.get()
        .uri("/{id}/tracking", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<TrackingWorkResponseDTO>>() {});
  }

  public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(UserResponseDTO.class);
  }

  public void deleteUser(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}
