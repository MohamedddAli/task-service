package io.unifonic.messaging;

import io.unifonic.event.TaskCompletedEvent;
import io.unifonic.event.TaskCreatedEvent;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;


@ApplicationScoped
public class TaskEventListener {

    // 1. RabbitMQ Consumer
    @SuppressWarnings("null")
    @Incoming("task-created-in")
    public void onTaskCreated(JsonObject json) {
        // Fix: Accept JsonObject, then convert it manually using mapTo()
        TaskCreatedEvent event = json.mapTo( TaskCreatedEvent.class);
        
        System.out.println("✅ [CONSUMER] RabbitMQ Received: Task ID " + event.taskId() 
            + " (Ref: " + event.correlationId() + ")");
    }

    // 2. Kafka Consumer
    // Kafka is strongly typed via our config, so we accept the Java Object
    @Incoming("task-completed-in")
    public void onTaskCompleted(TaskCompletedEvent event) {
        System.out.println("✅ [CONSUMER] Kafka Received: Task " + event.taskId() + " is DONE (Ref: " + event.correlationId() + ")");
    }
}