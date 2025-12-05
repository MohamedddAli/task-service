package io.unifonic.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.unifonic.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {
    
}
