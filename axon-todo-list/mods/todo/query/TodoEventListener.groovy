package query

import events.TodoCreatedEvent
import events.TodoMarkedAsCompleteEvent
import org.axonframework.domain.EventMessage
import org.axonframework.eventhandling.EventListenerProxy
import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.groovy.deploy.Container

/**
 * Listener that handles all events and creates messages to store the todoItems in the query database.
 *
 * @author Jettro Coenradie
 */
class TodoEventListener implements EventListenerProxy {
    Container container
    EventBus eventBus
    def logger

    TodoEventListener(EventBus eventBus, Container container) {
        this.container = container
        this.logger = container.logger
        this.eventBus = eventBus
    }

    Class<?> getTargetType() {
        return TodoCreatedEvent.class
    }

    void handle(EventMessage eventMessage) {
        def identifier = eventMessage.getIdentifier()
        def eventType = eventMessage.payloadType

        logger.debug "Received an event with id ${identifier} and type ${eventType}"

        switch (eventType) {
            case TodoCreatedEvent.class:
                handleTodoCreatedEvent(eventMessage)
                break
            case TodoMarkedAsCompleteEvent.class:
                handleTodoMarkedAsCompletedEvent(eventMessage)
                break
            default:
                logger.info "Cannot handle this event"
        }
    }

    /* Helper methods */

    private void handleTodoMarkedAsCompletedEvent(EventMessage eventMessage) {
        logger.info "Received a TodoMarkedAsCompleteEvent"
        def event = eventMessage.payload as TodoMarkedAsCompleteEvent
        def mongoAction = [
                "action": "update",
                "collection": "todos",
                "criteria": ["identifier": event.identifier.asString()],
                "objNew": ["\$set": ["completed": true]],
                "upsert": true,
                "multi": false]
        eventBus.send("vertx.mongo.persist", mongoAction) {
            eventBus.publish("message.all.clients", ["name": "TodoCompleted", "identifier": event.identifier.asString()])
        }
    }

    private void handleTodoCreatedEvent(EventMessage eventMessage) {
        logger.info "Received a TodoCreatedEvent"
        def event = eventMessage.payload as TodoCreatedEvent

        def todoItem = [
                "name": "TodoCreated",
                "todoText": event.todoText,
                "identifier": event.identifier.asString(),
                "completed": false]
        def mongoAction = ["action": "save", "collection": "todos", "document": todoItem]
        eventBus.send("vertx.mongo.persist", mongoAction) {
            eventBus.publish("message.all.clients", todoItem)
        }
    }
}
