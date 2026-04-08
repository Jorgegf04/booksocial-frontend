package com.example.booksocial_frontend.dto;

import java.time.LocalDate;

import com.example.booksocial_frontend.domain.Demographic;
import com.example.booksocial_frontend.domain.Genre;
import com.example.booksocial_frontend.domain.WorkType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkFilterDTO {

  private String title;
  private Genre genre;
  private WorkType type;
  private Demographic demographic;
  private Double minRating;
  private LocalDate publishedAfter;
  private LocalDate publishedBefore;
  private Long authorId;

}