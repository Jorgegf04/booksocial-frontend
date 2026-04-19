package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequestDTO {
    private String name;
    private String nationality;
    private LocalDate birthDate;
}
