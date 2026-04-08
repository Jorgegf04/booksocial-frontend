package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderLineResponseDTO {
  private Long productId;
  private String title;
  private Double price;
  private Integer quantity;
  private Double subtotal;
}
