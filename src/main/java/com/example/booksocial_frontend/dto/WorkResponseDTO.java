package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.booksocial_frontend.domain.Demographic;
import com.example.booksocial_frontend.domain.Genre;
import com.example.booksocial_frontend.domain.WorkType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para una obra del catálogo.
 * Los campos deben coincidir con WorkResponseDTO del backend.
 */
@Data
@NoArgsConstructor
public class WorkResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Genre genre;
    private WorkType type;
    private Demographic demographic;
    private LocalDate publicationDate;
    private String img;
    private Double averageRating;
    private List<String> authors;
}
