package commands

import aggregates.TodoIdentifier

/**
 * Command for creating a new todoItem with the provided todoText.
 *
 * @author Jettro Coenradie
 */
class CreateTodoCommand {
    TodoIdentifier identifier
    String todoText

    CreateTodoCommand(TodoIdentifier identifier, String todoText) {
        this.identifier = identifier
        this.todoText = todoText
    }
}
