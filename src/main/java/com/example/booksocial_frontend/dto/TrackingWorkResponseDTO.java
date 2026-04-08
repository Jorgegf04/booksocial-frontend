package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrackingWorkResponseDTO {
  private Long id;
  private Long userId;
  private String username;
  private Long workId;
  private String workTitle;
  private String status;
  private String statusLabel;
  private LocalDateTime date;
  private Boolean completed;
}
