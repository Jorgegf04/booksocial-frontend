package com.example.booksocial_frontend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.booksocial_frontend.dto.TrackingOrderResponseDTO;

@Service
public class TrackingOrderClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/tracking-orders");

  /**
   * Devuelve el último estado de seguimiento de un pedido,
   * o null si el pedido no tiene ningún tracking registrado aún.
   */
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
