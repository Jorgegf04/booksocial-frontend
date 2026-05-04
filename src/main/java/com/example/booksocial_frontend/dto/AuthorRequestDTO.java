package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import java.util.List;
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
    private String img;
    private List<Long> workIds;
}
