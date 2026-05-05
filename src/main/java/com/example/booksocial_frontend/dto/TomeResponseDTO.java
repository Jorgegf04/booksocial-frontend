package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TomeResponseDTO {
  private Long id;
  private Integer numberTome;
  private String title;
  private Long editionId;
  private String editionTitle;
}
