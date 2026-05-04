package com.example.booksocial_frontend.dto;

import java.time.LocalDate;

import java.util.List;

import com.example.booksocial_frontend.domain.Demographic;
import com.example.booksocial_frontend.domain.Genre;
import com.example.booksocial_frontend.domain.WorkType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkFilterDTO {

  private String title;
  private List<Genre> genres;
  private WorkType type;
  private Demographic demographic;
  private Double minRating;
  private LocalDate publishedAfter;
  private LocalDate publishedBefore;
  private Long authorId;

}