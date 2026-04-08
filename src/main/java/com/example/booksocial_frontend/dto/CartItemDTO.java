package com.example.booksocial_frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
  private Long productId;
  private String workTitle;
  private String editionTitle;
  private String editorialName;
  private String workImg;
  private Double price;
  private Integer quantity;

  public Double getSubtotal() {
    return price != null && quantity != null ? price * quantity : 0.0;
  }
}
