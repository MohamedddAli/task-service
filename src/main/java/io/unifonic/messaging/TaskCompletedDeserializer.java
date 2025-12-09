package io.unifonic.messaging;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.unifonic.event.TaskCompletedEvent;

// We extend the generic class and "lock in" the specific Type we want
public class TaskCompletedDeserializer extends ObjectMapperDeserializer<TaskCompletedEvent> {
    
    // This is the "No-Argument Constructor" that Kafka is looking for!
    public TaskCompletedDeserializer() {
        super(TaskCompletedEvent.class);
    }
}