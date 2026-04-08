package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.EventResponseDTO;

@Service
public class EventClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/events");

  public List<EventResponseDTO> getAllEvents() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<EventResponseDTO>>() {});
  }

  public List<EventResponseDTO> getUpcomingEvents() {
    return restClient.get()
        .uri("/upcoming")
        .retrieve()
        .body(new ParameterizedTypeReference<List<EventResponseDTO>>() {});
  }

  public EventResponseDTO getEventById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  public EventResponseDTO joinEvent(Long eventId, Long userId) {
    return restClient.post()
        .uri("/{id}/join?userId={userId}", eventId, userId)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  public EventResponseDTO leaveEvent(Long eventId, Long userId) {
    return restClient.delete()
        .uri("/{id}/leave?userId={userId}", eventId, userId)
        .retrieve()
        .body(EventResponseDTO.class);
  }
}
