package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.booksocial_frontend.domain.Demographic;
import com.example.booksocial_frontend.domain.Genre;
import com.example.booksocial_frontend.domain.WorkType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación y actualización de obras.
 *
 * Contiene los datos necesarios para registrar o modificar una obra
 * dentro del sistema, incluyendo los autores asociados mediante
 * sus identificadores.
 *
 * Este DTO es utilizado en las peticiones de la API REST.
 *
 * @author Jorge
 * @since 26/03/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkRequestDTO {

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