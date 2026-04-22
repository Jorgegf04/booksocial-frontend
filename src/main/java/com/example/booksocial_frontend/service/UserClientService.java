package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;

/**
 * Servicio cliente que comunica el frontend con los endpoints de usuario del backend.
 *
 * <p>Cubre operaciones CRUD sobre usuarios y el sistema de seguimiento social:</p>
 * <ul>
 *   <li>{@link #followUser(Long, Long)} — crea la relación follower → following.</li>
 *   <li>{@link #unfollowUser(Long, Long)} — elimina la relación.</li>
 *   <li>{@link #getFollowers(Long)} — lista de usuarios que siguen al userId indicado.</li>
 *   <li>{@link #getFollowing(Long)} — lista de usuarios a los que sigue el userId indicado.</li>
 * </ul>
 *
 * @author Jorge
 * @version 1.4
 * @since 2026-04-22
 */
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
