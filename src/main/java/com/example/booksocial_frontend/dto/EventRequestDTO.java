package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    private String title;
    private String description;
    private LocalDateTime date;
    private List<Long> userIds;
}
