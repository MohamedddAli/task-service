package io.unifonic.service;

import io.unifonic.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskService {

    private final TaskRepository taskRepository;
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
