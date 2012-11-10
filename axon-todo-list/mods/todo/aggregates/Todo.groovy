package aggregates

import org.axonframework.domain.DomainEventMessage
import org.axonframework.eventsourcing.AbstractEventSourcedAggregateRoot
import org.axonframework.eventsourcing.EventSourcedEntity
import events.TodoCreatedEvent

/**
 * @author Jettro Coenradie
 */
class Todo extends AbstractEventSourcedAggregateRoot {
    TodoIdentifier aggregateIdentifier

    Todo() {

    }

    Todo(String identifier, String todoText) {
        println "Trying to create a new ToDo item with id ${identifier}"
        apply(new TodoCreatedEvent(new TodoIdentifier(identifier), todoText))
    }

    @Override
    protected Iterable<? extends EventSourcedEntity> getChildEntities() {
        return null
    }

    @Override
    protected void handle(DomainEventMessage domainEventMessage) {
        this.aggregateIdentifier = ((TodoCreatedEvent) domainEventMessage.getPayload()).getIdentifier();
        println "Handle a new event ${((TodoCreatedEvent)domainEventMessage.getPayload()).todoText}"
    }

    Object getIdentifier() {
        return aggregateIdentifier
    }
}
