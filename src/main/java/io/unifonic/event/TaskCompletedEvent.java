package io.unifonic.event;

public record TaskCompletedEvent(
    String correlationId,
    Long taskId,
    String status
) {}