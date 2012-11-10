package events

import aggregates.TodoIdentifier

/**
 * @author Jettro Coenradie
 */
class TodoCreatedEvent {
    TodoIdentifier aggregateIdentifier;
    String todoText

    TodoCreatedEvent(TodoIdentifier identifier, String todoText) {
        this.aggregateIdentifier = identifier
        this.todoText = todoText
    }

    TodoIdentifier getIdentifier() {
        return aggregateIdentifier
    }
}
