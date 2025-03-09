package ru.alina.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alina.application.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
}
