package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrackingWorkRequestDTO {
  private Long userId;
  private Long workId;
  private String status;

  public TrackingWorkRequestDTO(Long userId, Long workId, String status) {
    this.userId = userId;
    this.workId = workId;
    this.status = status;
  }
}
