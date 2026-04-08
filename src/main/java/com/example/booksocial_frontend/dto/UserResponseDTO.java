package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDTO {
  private Long id;
  private String username;
  private String email;
  private String name;
  private String secondName;
  private String img;
  private LocalDate registrationDate;
  private Boolean active;
  private String role;
}
