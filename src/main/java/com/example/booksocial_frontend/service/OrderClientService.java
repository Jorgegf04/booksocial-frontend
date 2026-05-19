package com.example.booksocial_frontend.service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

@Service
public class OrderClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  @Autowired
  private SessionJwtInterceptor jwtInterceptor;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.builder()
        .baseUrl(apiBaseUrl + "/orders")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  public List<OrderResponseDTO> getAllOrders() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<OrderResponseDTO>>() {});
  }

  public OrderResponseDTO getOrderById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(OrderResponseDTO.class);
  }

  public List<OrderResponseDTO> getOrdersByUser(Long userId) {
    return restClient.get()
        .uri("/user/{userId}", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<OrderResponseDTO>>() {});
  }

  public OrderResponseDTO createOrder(Long userId, List<Map<String, Object>> lines) {
    return createOrder(userId, null, lines);
  }

  public OrderResponseDTO createOrder(Long userId, String guestEmail, List<Map<String, Object>> lines) {
    Map<String, Object> body = new HashMap<>();
    if (userId != null) {
      body.put("userId", userId);
    }
    if (guestEmail != null && !guestEmail.isBlank()) {
      body.put("guestEmail", guestEmail.trim());
    }
    body.put("orderLines", lines);

    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .body(OrderResponseDTO.class);
  }

  public void deleteOrder(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}
