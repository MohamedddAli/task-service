package io.unifonic.dto;

import io.unifonic.enums.TaskStatus;

public record TaskRequestDTO(
    String title,
    String description,
    TaskStatus status,
    String assignee
) {}