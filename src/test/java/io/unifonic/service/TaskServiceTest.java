package io.unifonic.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.unifonic.dto.TaskRequestDTO;
import io.unifonic.dto.TaskResponseDTO;
import io.unifonic.entity.Task;
import io.unifonic.enums.TaskStatus;
import io.unifonic.mapper.TaskMapper;
import io.unifonic.repository.TaskRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@QuarkusTest
public class TaskServiceTest {

    @Inject
    TaskService service; // The class we are testing

    @InjectMock
    TaskRepository repository; // We mock this (fake database)

    @InjectMock
    TaskMapper mapper; // We mock this (fake translator)

    @Test
    void create_ShouldReturnResponse_WhenValidRequest() {
        // 1. SETUP (Prepare data)
        TaskRequestDTO request = new TaskRequestDTO("Title", "Desc", TaskStatus.TODO, "User");
        Task taskEntity = new Task(); // Empty entity
        TaskResponseDTO expectedResponse = new TaskResponseDTO(1L, "Title", "Desc", TaskStatus.TODO, "User", null);

        // 2. MOCKING (Teach the mocks what to do)
        when(mapper.toEntity(request)).thenReturn(taskEntity);
        // persist returns void, so we don't need to mock it, strictly speaking, 
        // but Mockito handles void methods automatically.
        when(mapper.toResponse(taskEntity)).thenReturn(expectedResponse);

        // 3. EXECUTE (Run the method)
        TaskResponseDTO actualResponse = service.create(request);

        // 4. VERIFY (Check results)
        assertNotNull(actualResponse);
        assertEquals("Title", actualResponse.title());
        
        // Verify that the repository.persist() method was actually called exactly once
        verify(repository, times(1)).persist(taskEntity);
    }

    @Test
    void getById_ShouldReturnTask_WhenIdExists() {
        // 1. Setup
        Long id = 1L;
        Task taskEntity = new Task();
        taskEntity.id = id;
        TaskResponseDTO expectedResponse = new TaskResponseDTO(id, "Title", "Desc", TaskStatus.TODO, "User", null);

        // 2. Mocking
        when(repository.findById(id)).thenReturn(taskEntity);
        when(mapper.toResponse(taskEntity)).thenReturn(expectedResponse);

        // 3. Execute
        TaskResponseDTO result = service.getById(id);

        // 4. Verify
        assertEquals(id, result.id());
    }

    @Test
    void getById_ShouldThrow404_WhenIdDoesNotExist() {
        // 1. Setup
        Long nonExistentId = 99L;

        // 2. Mocking - Repository returns null
        when(repository.findById(nonExistentId)).thenReturn(null);

        // 3. Execute & Verify (Expecting an exception)
        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            service.getById(nonExistentId);
        });

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void update_ShouldUpdateAndReturn_WhenTaskExists() {
        // 1. Setup
        Long id = 1L;
        TaskRequestDTO updateRequest = new TaskRequestDTO("New Title", "New Desc", TaskStatus.DONE, "New User");
        Task existingTask = new Task();
        existingTask.id = id;
        
        TaskResponseDTO expectedResponse = new TaskResponseDTO(id, "New Title", "New Desc", TaskStatus.DONE, "New User", null);

        // 2. Mocking
        when(repository.findById(id)).thenReturn(existingTask);
        // We assume mapper.updateEntityFromDTO modifies the entity in place, so we don't need a return value
        when(mapper.toResponse(existingTask)).thenReturn(expectedResponse);

        // 3. Execute
        TaskResponseDTO result = service.update(id, updateRequest);

        // 4. Verify
        // Ensure the mapper was called to update the fields
        verify(mapper).updateEntityFromDTO(updateRequest, existingTask);
        assertEquals("New Title", result.title());
    }

    @Test
    void delete_ShouldThrow404_WhenTaskNotFound() {
        Long id = 99L;
        
        // Mocking: deleteById returns FALSE (meaning nothing was deleted)
        when(repository.deleteById(id)).thenReturn(false);

        assertThrows(WebApplicationException.class, () -> {
            service.delete(id);
        });
    }
}