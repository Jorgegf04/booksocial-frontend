package com.example.booksocial_frontend.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriptionResponseDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean activated;
    private Long userId;
}
