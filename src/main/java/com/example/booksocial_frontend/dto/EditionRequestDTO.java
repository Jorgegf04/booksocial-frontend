package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditionRequestDTO {
    private String isbn;
    private LocalDate editionDate;
    private Long workId;
    private Long editorialId;
    private String title;
    private Integer totalTomes;
}
