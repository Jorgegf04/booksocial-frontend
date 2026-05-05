package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterRequestDTO {
  private Integer chapterNumber;
  private String title;
  private Long tomeId;
}
