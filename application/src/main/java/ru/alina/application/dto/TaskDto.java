package ru.alina.application.dto;

import lombok.*;
import ru.alina.application.entity.Status;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Long userId;
}
