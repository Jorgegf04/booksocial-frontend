package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrackingOrderResponseDTO {
  private Long id;
  private String status;
  private String statusLabel;
  private LocalDateTime date;
  private Long orderId;
}
