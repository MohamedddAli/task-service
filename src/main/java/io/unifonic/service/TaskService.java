package io.unifonic.service;

import io.unifonic.dto.TaskRequestDTO;
import io.unifonic.dto.TaskResponseDTO;
import io.unifonic.entity.Task;
import io.unifonic.mapper.TaskMapper;
import io.unifonic.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // POST (Create)
    @Transactional
    public TaskResponseDTO create(TaskRequestDTO request) {
        Task task = mapper.toEntity(request);
        repository.persist(task);
        return mapper.toResponse(task);
    }

    // GET (All)
    public List<TaskResponseDTO> getAll() {
        return repository.listAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // GET (By ID)
    public TaskResponseDTO getById(Long id) {
        Task task = repository.findById(id);
        if (task == null) {
            // Returns HTTP 404 automatically
            throw new WebApplicationException("Task not found with id: " + id, 404);
        }
        return mapper.toResponse(task);
    }

    // PUT (Update)
    @Transactional
    public TaskResponseDTO update(Long id, TaskRequestDTO request) {
        // 1. Find existing task
        Task task = repository.findById(id);
        if (task == null) {
            throw new WebApplicationException("Task not found with id: " + id, 404);
        }

        // 2. Update fields using Mapper
        mapper.updateEntityFromDTO(request, task);
        
        // 3. No need to call 'persist'. Since we are inside @Transactional,
        // Hibernate automatically detects changes to the entity and saves them.
        
        return mapper.toResponse(task);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new WebApplicationException("Task not found with id: " + id, 404);
        }
    }
}