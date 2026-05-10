package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.CreateUserRequestDTO;
import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;

import jakarta.annotation.PostConstruct;

@Service
public class UserClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.create(apiBaseUrl + "/users");
  }

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

  public List<UserResponseDTO> getFollowers(Long userId) {
    return restClient.get()
        .uri("/{id}/followers", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<UserResponseDTO>>() {});
  }

  public List<UserResponseDTO> getFollowing(Long userId) {
    return restClient.get()
        .uri("/{id}/following", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<UserResponseDTO>>() {});
  }

  public UserResponseDTO createUser(CreateUserRequestDTO dto) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(dto)
        .retrieve()
        .body(UserResponseDTO.class);
  }

  public void followUser(Long followerId, Long targetId) {
    restClient.post()
        .uri("/{id}/follow/{targetId}", followerId, targetId)
        .retrieve()
        .toBodilessEntity();
  }

  public void unfollowUser(Long followerId, Long targetId) {
    restClient.delete()
        .uri("/{id}/follow/{targetId}", followerId, targetId)
        .retrieve()
        .toBodilessEntity();
  }
}
