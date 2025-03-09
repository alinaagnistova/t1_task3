package ru.alina.application.util;

import org.springframework.stereotype.Component;
import ru.alina.application.dto.TaskDto;

@Component
public class TaskEmailBuilder {

    public String build(TaskDto taskDto){
        return String.format(
                "New task notification:\n\n" +
                        "ID: %d\n" +
                        "Title: %s\n" +
                        "Description: %s\n" +
                        "Status: %s\n" +
                        "User ID: %d\n",
                taskDto.getId(),
                taskDto.getTitle(),
                taskDto.getDescription(),
                taskDto.getStatus(),
                taskDto.getUserId()
        );
    }
}
