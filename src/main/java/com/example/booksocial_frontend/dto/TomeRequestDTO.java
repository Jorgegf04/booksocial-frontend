package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TomeRequestDTO {
  private Integer numberTome;
  private String title;
  private Long editionId;
}
