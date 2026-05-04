package com.example.booksocial_frontend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.booksocial_frontend.dto.TrackingOrderResponseDTO;

import jakarta.annotation.PostConstruct;

@Service
public class TrackingOrderClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.create(apiBaseUrl + "/tracking-orders");
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
}
