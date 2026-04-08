package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorResponseDTO {
  private Long id;
  private String name;
  private String nationality;
  private LocalDate birthDate;
  private List<WorkResponseDTO> works;
}
