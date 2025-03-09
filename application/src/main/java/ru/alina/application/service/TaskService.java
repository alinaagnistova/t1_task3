package ru.alina.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alina.application.aspect.annotation.CustomExceptionHandling;
import ru.alina.application.aspect.annotation.CustomLogging;
import ru.alina.application.dto.TaskDto;
import ru.alina.application.entity.Task;
import ru.alina.application.exception.TaskNotFoundException;
import ru.alina.application.kafka.task.TaskProducer;
import ru.alina.application.mapper.TaskMapper;
import ru.alina.application.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskProducer taskProducer;

    @CustomLogging
    public TaskDto addTask(TaskDto taskDto) {
        Task task = taskMapper.toTaskEntity(taskDto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskDto(savedTask);
    }

    @CustomExceptionHandling
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toTaskDto(task);
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toTaskDto)
                .collect(Collectors.toList());
    }

    @CustomExceptionHandling
    public TaskDto updateTaskById(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        boolean isNeedToUpdate = false;
        if (!task.getTitle().equals(taskDto.getTitle())) {
            task.setTitle(taskDto.getTitle());
            isNeedToUpdate = true;
        }
        if (!task.getDescription().equals(taskDto.getDescription())) {
            task.setDescription(taskDto.getDescription());
            isNeedToUpdate = true;
        }
        if (!task.getUserId().equals(taskDto.getUserId())) {
            task.setUserId(taskDto.getUserId());
            isNeedToUpdate = true;
        }
        if (!task.getStatus().equals(taskDto.getStatus())) {
            task.setStatus(taskDto.getStatus());
            isNeedToUpdate = true;
            taskProducer.send(taskMapper.toTaskDto(task));
        }

        return  isNeedToUpdate ? taskMapper.toTaskDto(taskRepository.save(task)) : taskMapper.toTaskDto(task);
    }

    @CustomExceptionHandling
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.deleteById(id);
    }

}
