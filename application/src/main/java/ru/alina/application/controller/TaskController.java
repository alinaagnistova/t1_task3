package ru.alina.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.alina.application.dto.TaskDto;
import ru.alina.application.service.TaskService;


import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        return taskService.addTask(taskDto);
    }

    @GetMapping("/{id}")
    public TaskDto getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        return taskService.updateTaskById(id, taskDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }


}
