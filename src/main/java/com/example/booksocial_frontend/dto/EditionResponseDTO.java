package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EditionResponseDTO {

  private Long id;
  private String isbn;
  private LocalDate editionDate;
  private String title;
  private Integer totalTomes;
  private Long workId;
  private String workTitle;
  private Long editorialId;
  private String editorialName;
}
