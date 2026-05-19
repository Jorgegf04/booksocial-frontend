package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.EventRequestDTO;
import com.example.booksocial_frontend.dto.EventResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

@Service
public class EventClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  @Autowired
  private SessionJwtInterceptor jwtInterceptor;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.builder()
        .baseUrl(apiBaseUrl + "/events")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

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

  public EventResponseDTO createEvent(EventRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  public EventResponseDTO updateEvent(Long id, EventRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  public void deleteEvent(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}
