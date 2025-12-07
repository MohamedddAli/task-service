package io.unifonic.event;

public record TaskCreatedEvent(
    String correlationId,
    Long taskId,
    String title,
    String assignee
) {}