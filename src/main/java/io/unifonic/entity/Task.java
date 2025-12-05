package io.unifonic.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.unifonic.enums.TaskStatus;

import java.time.Instant;

@Entity
public class Task {

    @Id
    @GeneratedValue
    public Long id;

    public String title;
    
    public String description;

    // Stores the Enum as a String in the DB (e.g., "TODO") instead of a number (0)
    @Enumerated(EnumType.STRING) 
    public TaskStatus status;

    public String assignee;

    // Automatically sets the time when the row is created
    @CreationTimestamp
    public Instant createdAt;

    // Automatically updates the time whenever the row is modified
    @UpdateTimestamp
    public Instant updatedAt;
}