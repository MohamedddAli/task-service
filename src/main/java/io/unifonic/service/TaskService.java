package io.unifonic.service;

import io.unifonic.dto.TaskRequestDTO;
import io.unifonic.dto.TaskResponseDTO;
import io.unifonic.entity.Task;
import io.unifonic.enums.TaskStatus;
import io.unifonic.event.TaskCompletedEvent;
import io.unifonic.event.TaskCreatedEvent;
import io.unifonic.mapper.TaskMapper;
import io.unifonic.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;


        // 1. Inject RabbitMQ Emitter
    @Channel("task-created-out")
    Emitter<TaskCreatedEvent> createdEmitter;

    // 2. Inject Kafka Emitter
    @Channel("task-completed-out")
    Emitter<TaskCompletedEvent> completedEmitter;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // POST (Create)
    @Transactional
    public TaskResponseDTO create(TaskRequestDTO request) {
        Task task = mapper.toEntity(request);
        repository.persist(task);

        // 3. Publish "Task Created" to RabbitMQ
        String correlationId = UUID.randomUUID().toString();
        TaskCreatedEvent event = new TaskCreatedEvent(
            correlationId, task.id, task.title, task.assignee
        );
        
        createdEmitter.send(event);
        System.out.println("✅>>> [PRODUCER] RabbitMQ Sent: " + event);

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
        Task task = repository.findById(id);
        if (task == null) {
            throw new WebApplicationException("Task not found", 404);
        }

        mapper.updateEntityFromDTO(request, task);

        // 4. Publish "Task Completed" to Kafka (Only if status is DONE)
        if (task.status == TaskStatus.DONE) {
            String correlationId = UUID.randomUUID().toString();
            TaskCompletedEvent event = new TaskCompletedEvent(
                correlationId, task.id, "DONE"
            );
            
            completedEmitter.send(event);
            System.out.println("✅>>> [PRODUCER] Kafka Sent: " + event);
        }

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