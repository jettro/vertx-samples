package commands

/**
 * @author Jettro Coenradie
 */
class CreateTodoCommand {
    String identifier
    String todoText

    CreateTodoCommand(String identifier, String todoText) {
        this.identifier = identifier
        this.todoText = todoText
    }
}
