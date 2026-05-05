package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.ProductRequestDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;

import jakarta.annotation.PostConstruct;

@Service
public class ProductClientService {

  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.create(apiBaseUrl + "/products");
  }

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

  public List<ProductResponseDTO> getProductsByEdition(Long editionId) {
    return restClient.get()
        .uri("/edition/{editionId}", editionId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<ProductResponseDTO>>() {});
  }

  public ProductResponseDTO createProduct(Long editionId, Double price, Integer stock) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ProductRequestDTO(price, stock, editionId))
        .retrieve()
        .body(ProductResponseDTO.class);
  }

  public ProductResponseDTO updateProduct(Long id, Double price, Integer stock, Long editionId) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ProductRequestDTO(price, stock, editionId))
        .retrieve()
        .body(ProductResponseDTO.class);
  }
}
