package com.example.booksocial_frontend.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.OrderResponseDTO;

import jakarta.annotation.PostConstruct;

@Service
public class OrderClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.create(apiBaseUrl + "/orders");
  }

  public List<OrderResponseDTO> getAllOrders() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<OrderResponseDTO>>() {});
  }

  public List<OrderResponseDTO> getOrdersByUser(Long userId) {
    return restClient.get()
        .uri("/user/{userId}", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<OrderResponseDTO>>() {});
  }

  public OrderResponseDTO createOrder(Long userId, List<Map<String, Object>> lines) {
    var body = Map.of("userId", userId, "orderLines", lines);
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .body(OrderResponseDTO.class);
  }
}
