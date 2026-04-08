package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentResponseDTO {

  private Long id;
  private String content;
  private LocalDateTime date;
  private LocalDateTime updatedAt;
  private Boolean edited;
  private Long userId;
  private String username;
  private Long workId;
  private String workTitle;
  private Long parentId;
  private List<CommentResponseDTO> replies;
}
