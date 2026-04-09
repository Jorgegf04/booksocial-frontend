package com.example.booksocial_frontend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReactionResponseDTO {
    private Long id;
    private LocalDateTime date;
    private Long userId;
    private String username;
    private Long commentId;
    private Boolean liked;
}
