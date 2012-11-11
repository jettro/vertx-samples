package aggregates

import org.axonframework.domain.DomainEventMessage
import org.axonframework.eventsourcing.AbstractEventSourcedAggregateRoot
import org.axonframework.eventsourcing.EventSourcedEntity
import events.TodoCreatedEvent
import events.TodoMarkedAsCompleteEvent

/**
 * Aggregate root for todoItems
 *
 * @author Jettro Coenradie
 */
class Todo extends AbstractEventSourcedAggregateRoot {
    TodoIdentifier aggregateIdentifier
    TodoStatus status

    Todo() {}

    Todo(TodoIdentifier identifier, String todoText) {
        apply(new TodoCreatedEvent(identifier, todoText))
    }

    void markAsComplete() {
        apply(new TodoMarkedAsCompleteEvent(aggregateIdentifier))
    }

    @Override
    protected Iterable<? extends EventSourcedEntity> getChildEntities() {
        return null
    }

    @Override
    protected void handle(DomainEventMessage domainEventMessage) {
        switch (domainEventMessage.payloadType) {
            case TodoCreatedEvent.class:
                this.aggregateIdentifier = (domainEventMessage.payload as TodoCreatedEvent).getIdentifier();
                this.status = TodoStatus.open
                break
            case TodoMarkedAsCompleteEvent.class:
                this.status = TodoStatus.completed
                break
            default:
                throw new IllegalArgumentException("Event ${domainEventMessage.payloadType} is not supported")
        }

    }

    Object getIdentifier() {
        return aggregateIdentifier
    }
}

enum TodoStatus {
    open,completed
}
