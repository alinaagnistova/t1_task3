package ru.alina.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.alina.application.config.EmailProperties;
import ru.alina.application.dto.TaskDto;
import ru.alina.application.util.TaskEmailBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    private final TaskEmailBuilder taskEmailBuilder;

    private final EmailProperties emailProperties;

    public void notify(TaskDto taskDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        String recipient = emailProperties.getRecipient();
        message.setTo(recipient);
        message.setSubject(emailProperties.getSubject());
        message.setText(taskEmailBuilder.build(taskDto));
        mailSender.send(message);
        log.info("Notification sent: Task ID = {} to = {}", taskDto.getId(), recipient);
    }

}
