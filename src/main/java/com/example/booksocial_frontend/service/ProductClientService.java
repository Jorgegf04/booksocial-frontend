package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.ProductResponseDTO;

@Service
public class ProductClientService {

  private final RestClient restClient = RestClient.create("http://localhost:9999/api/products");

  public List<ProductResponseDTO> getAvailableProducts() {
    return restClient.get()
        .uri("/available")
        .retrieve()
        .body(new ParameterizedTypeReference<List<ProductResponseDTO>>() {});
  }

  public List<ProductResponseDTO> getProductsByWork(Long workId) {
    return restClient.get()
        .uri("/work/{workId}", workId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<ProductResponseDTO>>() {});
  }

  public ProductResponseDTO getProductById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(ProductResponseDTO.class);
  }
}
