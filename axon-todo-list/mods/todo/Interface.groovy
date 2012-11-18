import aggregates.Todo
import aggregates.TodoCommandHandler
import aggregates.TodoIdentifier
import com.mongodb.Mongo
import com.thoughtworks.xstream.XStream
import commands.CreateTodoCommand
import commands.MarkTodoAsCompleteCommand
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.axonframework.eventhandling.SimpleEventBus
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventstore.mongo.DefaultMongoTemplate
import org.axonframework.eventstore.mongo.MongoEventStore
import org.axonframework.eventstore.mongo.MongoTemplate
import org.axonframework.serializer.bson.DBObjectXStreamSerializer
import org.vertx.groovy.core.eventbus.EventBus
import query.TodoEventListener

def log = container.logger

log.info "The Todo mod is started."

/* Axon infrastructure configuration */
CommandBus commandBus = new SimpleCommandBus()
CommandGateway gateway = new DefaultCommandGateway(commandBus)
org.axonframework.eventhandling.EventBus axonEventBus = new SimpleEventBus()

def xstream = new XStream()
xstream.classLoader = getClass().classLoader
def serializer = new DBObjectXStreamSerializer(xstream)

MongoTemplate mongoTemplate = new DefaultMongoTemplate(new Mongo(), "axon-todo", "domain", "saga", null, null)
MongoEventStore eventStore = new MongoEventStore(serializer, mongoTemplate)

def todoRepository = new EventSourcingRepository<Todo>(Todo.class) {}
todoRepository.setEventStore(eventStore)
todoRepository.setEventBus(axonEventBus)

/* Register the command handlers with the command bus */
def handler = new TodoCommandHandler(todoRepository, container)
commandBus.subscribe("commands.CreateTodoCommand", handler)
commandBus.subscribe("commands.MarkTodoAsCompleteCommand", handler)

/* VERTX Configuration */
EventBus eventBus = vertx.eventBus

/* Register the listeners with the event bus for the query database */
axonEventBus.subscribe(new TodoEventListener(eventBus, container))

eventBus.registerHandler("command.todo.create") { message ->
    message.reply(["message": "We have received a new ToDo"])
    def identifier = new TodoIdentifier()
    gateway.send(new CreateTodoCommand(identifier, message.body.todoText))
}

eventBus.registerHandler("command.todo.markcompleted") { message ->
    message.reply(["message": "We have received a ToDo that is completed"])
    def identifier = new TodoIdentifier(message.body.identifier)
    gateway.send(new MarkTodoAsCompleteCommand(identifier))
}
