package io.unifonic.mapper;

import io.unifonic.dto.TaskRequestDTO;
import io.unifonic.dto.TaskResponseDTO;
import io.unifonic.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskMapper {

    // 1. Create New Entity
    public Task toEntity(TaskRequestDTO request) {
        if (request == null) return null;
        
        Task task = new Task();
        // Use the shared update logic to fill fields
        updateEntityFromDTO(request, task);
        return task;
    }

    // 2. Map Entity to Response
    public TaskResponseDTO toResponse(Task task) {
        if (task == null) return null;

        return new TaskResponseDTO(
            task.id,
            task.title,
            task.description,
            task.status,
            task.assignee,
            task.createdAt
        );
    }

    
    // Copies data from DTO into an EXISTING database object
    public void updateEntityFromDTO(TaskRequestDTO request, Task task) {
        if (request == null || task == null) return;

        task.title = request.title();
        task.description = request.description();
        task.status = request.status();
        task.assignee = request.assignee();
        // Note: We do NOT update id or createdAt here
    }
}