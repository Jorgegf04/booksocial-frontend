package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponseDTO {
  private Long id;
  private Double price;
  private Integer stock;
  private Long editionId;
  private String editionTitle;
  private String editionIsbn;
  private Integer totalTomes;
  private Long workId;
  private String workTitle;
  private String editorialName;
}
