package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventResponseDTO {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime date;
  private List<Long> userIds;
  private List<String> usernames;
  private Integer totalParticipants;
}
