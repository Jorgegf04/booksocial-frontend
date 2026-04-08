package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponseDTO {
  private Long id;
  private LocalDateTime date;
  private Double total;
  private Long userId;
  private String username;
  private Integer totalItems;
  private List<OrderLineResponseDTO> orderLines;
}
