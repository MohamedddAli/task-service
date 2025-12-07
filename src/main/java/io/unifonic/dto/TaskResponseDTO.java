package io.unifonic.dto;

import java.time.Instant;

import io.unifonic.enums.TaskStatus;

public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    TaskStatus status,
    String assignee,
    Instant createdAt
) {}