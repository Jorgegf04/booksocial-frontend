package com.example.booksocial_frontend.dto;

import lombok.Data;

// dto/auth/LoginRequestDTO

@Data
public class JwtResponseDTO {
  private String token;
  private String type;
  private Long userId;
  private String username;
  private String role;
}