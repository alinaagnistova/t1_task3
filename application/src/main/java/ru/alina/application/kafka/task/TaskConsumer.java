package ru.alina.application.kafka.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.alina.application.dto.TaskDto;
import ru.alina.application.service.NotificationService;


import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.task-status}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(List<TaskDto> taskDtoList, Acknowledgment ack) {
        log.info("Received {} Task Updates", taskDtoList.size());
        try {
            taskDtoList.forEach(taskDto -> {
                log.info("Processing Task Update: ID = {}, Status = {}", taskDto.getId(), taskDto.getStatus());
                notificationService.notify(taskDto);
            });
            ack.acknowledge();
            log.info("Successfully processed and acknowledged {} messages.", taskDtoList.size());
        } catch (Exception e) {
            log.error("Error processing batch of messages, skipping acknowledgment. Cause: {}", e.getMessage(), e);
        }
    }

}
