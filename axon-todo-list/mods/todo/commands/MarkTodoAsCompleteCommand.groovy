package commands

import aggregates.TodoIdentifier

/**
 * Command that marks the todoItem with the provided identifier as complete.
 *
 * @author Jettro Coenradie
 */
class MarkTodoAsCompleteCommand {
    TodoIdentifier identifier

    MarkTodoAsCompleteCommand(TodoIdentifier identifier) {
        this.identifier = identifier
    }
}
