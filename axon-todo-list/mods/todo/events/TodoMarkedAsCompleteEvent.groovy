package events

import aggregates.TodoIdentifier

/**
 * @author Jettro Coenradie
 */
class TodoMarkedAsCompleteEvent {
    TodoIdentifier aggregateIdentifier

    TodoMarkedAsCompleteEvent(TodoIdentifier identifier) {
        this.aggregateIdentifier = identifier
    }

    TodoIdentifier getIdentifier() {
        return aggregateIdentifier
    }

}
