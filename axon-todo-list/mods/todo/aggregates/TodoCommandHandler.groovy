package aggregates

import commands.CreateTodoCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.repository.Repository
import org.axonframework.unitofwork.UnitOfWork
import org.vertx.groovy.deploy.Container
import commands.MarkTodoAsCompleteCommand

/**
 * This handler is used for handling all commands related to todoItems.
 *
 * @author Jettro Coenradie
 */
class TodoCommandHandler implements CommandHandler {
    def log = container.logger

    Repository<Todo> repository

    TodoCommandHandler(Repository<Todo> repository, Container container) {
        this.repository = repository
    }

    Object handle(CommandMessage commandMessage, UnitOfWork unitOfWork) {
        switch(commandMessage.payloadType) {
            case CreateTodoCommand.class:
                CreateTodoCommand command = commandMessage.payload as CreateTodoCommand
                log.info "The received command is ${command.todoText}"
                def todo = new Todo(command.identifier, command.todoText)
                repository.add(todo)
                return todo
            case MarkTodoAsCompleteCommand.class:
                MarkTodoAsCompleteCommand command = commandMessage.payload as MarkTodoAsCompleteCommand
                def load = repository.load(command.identifier)
                load.markAsComplete()
                return load
            default:
                log.info "Received a command of type we cannot handle: ${commandMessage.payloadType}"
                return null
        }
    }
}
