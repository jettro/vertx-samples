def log = container.logger

def persisterConfig = ["db_name": "axon_todo_list", "address": "vertx.mongo.persist"]

container.with {
    deployModule('vertx.mongo-persistor-v1.2', persisterConfig, 1) {
        log.info "App: The mongo-persister module is deployed."
    }
    deployModule('web') {
        log.info "App: The web module is deployed."
    }
    deployModule('todo') {
        log.info "App: The Todo module is deployed."
    }
}