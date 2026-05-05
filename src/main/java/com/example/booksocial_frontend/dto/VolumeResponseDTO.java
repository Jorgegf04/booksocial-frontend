package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VolumeResponseDTO {
  private Long id;
  private Integer volumeNumber;
  private String title;
  private Long editionId;
  private String editionTitle;
}
