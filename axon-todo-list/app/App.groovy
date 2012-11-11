import org.vertx.groovy.core.eventbus.EventBus

def log = container.logger

EventBus eventBus = vertx.eventBus

container.with {
    deployModule('web') {
        log.info "App: The web module is deployed."
    }
    deployModule('todo') {
        log.info "App: The Todo module is deployed."
        eventBus.publish("command.todo.create",["todoText":"This is a new TODO"])
    }
}