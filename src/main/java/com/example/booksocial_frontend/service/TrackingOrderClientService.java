package com.example.booksocial_frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.booksocial_frontend.dto.TrackingOrderRequestDTO;
import com.example.booksocial_frontend.dto.TrackingOrderResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

@Service
public class TrackingOrderClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  @Autowired
  private SessionJwtInterceptor jwtInterceptor;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.builder()
        .baseUrl(apiBaseUrl + "/tracking-orders")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  public TrackingOrderResponseDTO getLatestByOrder(Long orderId) {
    try {
      return restClient.get()
          .uri("/order/{orderId}/latest", orderId)
          .retrieve()
          .body(TrackingOrderResponseDTO.class);
    } catch (RestClientResponseException e) {
      return null;
    }
  }

  public TrackingOrderResponseDTO createTracking(Long orderId, String status) {
    TrackingOrderRequestDTO dto = new TrackingOrderRequestDTO(orderId, status);
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(dto)
        .retrieve()
        .body(TrackingOrderResponseDTO.class);
  }
}
