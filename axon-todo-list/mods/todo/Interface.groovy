import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.unitofwork.UnitOfWork
import org.vertx.groovy.core.eventbus.EventBus
import commands.CreateTodoCommand
import aggregates.Todo
import org.axonframework.eventhandling.SimpleEventBus
import org.vertx.groovy.core.eventbus.EventBus
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventstore.mongo.MongoEventStore
import org.axonframework.eventstore.mongo.MongoTemplate
import org.axonframework.eventstore.mongo.DefaultMongoTemplate
import com.mongodb.Mongo
import org.axonframework.domain.EventMessage
import events.TodoCreatedEvent

def log = container.logger

log.info "The Todo mod is started."

CommandBus commandBus = new SimpleCommandBus()
commandBus.subscribe("commands.CreateTodoCommand", new CommandHandler(){

    Object handle(CommandMessage commandMessage, UnitOfWork unitOfWork) {
        CreateTodoCommand command = commandMessage.payload as CreateTodoCommand
        log.info "The received command is ${command.todoText}"
        new Todo(command.identifier,command.todoText)
        return null
    }
})

org.axonframework.eventhandling.EventBus axonEventBus = new SimpleEventBus()
axonEventBus.subscribe(new org.axonframework.eventhandling.EventListenerProxy(){

    void handle(EventMessage eventMessage) {
        println "We received an event ${eventMessage.getPayload().todoText}"
    }

    Class<?> getTargetType() {
        return TodoCreatedEvent.class
    }
})

def todoRepository = new EventSourcingRepository<Todo>(Todo.class) {}
MongoTemplate mongoTemplate = new DefaultMongoTemplate(new Mongo())
MongoEventStore eventStore = new MongoEventStore(mongoTemplate)
todoRepository.setEventStore(eventStore)
todoRepository.setEventBus(axonEventBus)

CommandGateway gateway = new DefaultCommandGateway(commandBus)


// VERTX
EventBus eventBus = vertx.eventBus

eventBus.registerHandler("command.todo.create") {message ->
    gateway.send(new CreateTodoCommand("id-1", message.body.todoText))
}
