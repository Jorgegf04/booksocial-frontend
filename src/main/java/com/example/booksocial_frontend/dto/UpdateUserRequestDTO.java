package com.example.booksocial_frontend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {
  private String username;
  private String email;
  private String name;
  private String secondName;
  private String img;
}
