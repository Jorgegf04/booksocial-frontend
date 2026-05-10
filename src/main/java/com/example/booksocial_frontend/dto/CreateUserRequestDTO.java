package com.example.booksocial_frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
    private String username;
    private String password;
    private String email;
    private String name;
    private String secondName;
    private String role;
}
