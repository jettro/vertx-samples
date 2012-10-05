import groovy.json.JsonSlurper
import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.impl.DefaultHttpServer

/**
 * @author Jettro Coenradie
 */
def log = container.logger
EventBus eventBus = vertx.eventBus
def slurper = new JsonSlurper()

eventBus.registerHandler("message.send.notification") {message ->
    sendMessageToEventBus(message.body, "notification", log, slurper, eventBus)
}

eventBus.registerHandler("message.send.invitation") {message ->
    sendMessageToEventBus(message.body, "invitation", log, slurper, eventBus)
}

def sendMessageToEventBus(message, type, log, slurper, eventBus) {
    log.info "Received a message to send to a client ${message} of type ${type}"
    def theMessage = "{\"message\":\"${message}\",\"type\":\"${type}\"}"
    def jsonMessage = slurper.parseText(theMessage)
    eventBus.publish("message.forclients", jsonMessage)
}


RouteMatcher routeMatcher = new RouteMatcher()

routeMatcher.get("/") {req ->
    req.response.sendFile "static/index.html"
}

routeMatcher.post("/rest") {req ->
    req.bodyHandler { body ->
        eventBus.send("notification.received", body.toString())
    }

    req.response.putHeader("Content-Type", "application/json")
    req.response.end("{\"status\":\"RECEIVED\"}")

}

routeMatcher.getWithRegEx("^\\/static\\/.*") { req ->
    req.response.sendFile(req.path.substring(1))
}

DefaultHttpServer server = vertx.createHttpServer()
server.requestHandler(routeMatcher.asClosure())

vertx.createSockJSServer(server).bridge(prefix: '/eventbus', [[:]], [[:]])

server.listen(8080)

log.info "The http servers is started"