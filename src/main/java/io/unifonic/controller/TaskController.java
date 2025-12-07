package io.unifonic.controller;

import io.unifonic.dto.TaskRequestDTO;
import io.unifonic.dto.TaskResponseDTO;
import io.unifonic.service.TaskService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // 1. Create
    @POST
    @ResponseStatus(201) // change it from default 200(OK) to 201(Created)
    public TaskResponseDTO create(TaskRequestDTO request) {
        // Now you can just return the object directly!
        return service.create(request);
    }

    // 2. Get All
    @GET
    public List<TaskResponseDTO> getAll() {
        return service.getAll();
    }

    // 3. Get By ID
    @GET
    @Path("/{id}")
    public TaskResponseDTO getById(@PathParam("id") Long id) {
        return service.getById(id);
    }

    // 4. Update
    @PUT
    @Path("/{id}")
    public TaskResponseDTO update(@PathParam("id") Long id, TaskRequestDTO request) {
        return service.update(id, request);
    }

    // 5. Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        // Return 204 No Content (Standard for Delete)
        return Response.noContent().build();
    }
}