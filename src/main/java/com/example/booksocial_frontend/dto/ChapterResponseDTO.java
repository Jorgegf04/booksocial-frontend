package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterResponseDTO {
  private Long id;
  private Integer chapterNumber;
  private String title;
  private Long tomeId;
  private String tomeTitle;
}
