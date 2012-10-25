import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.impl.DefaultHttpServer

/**
 * Web server to handle all incoming web requests.
 *
 * @author Jettro Coenradie
 */
def log = container.logger
EventBus eventBus = vertx.eventBus

eventBus.registerHandler("message.send.invitation") {message ->
    def theMessage = message.body
    theMessage.put("type", "invitation")
    log.info "Received a message to send to a client ${theMessage}"
    eventBus.publish("message.forclients", theMessage)
}

RouteMatcher routeMatcher = new RouteMatcher()

routeMatcher.get("/") {req ->
    req.response.sendFile "static/index.html"
}

routeMatcher.post("/rest") {req ->
    req.bodyHandler { body ->
//        def notification =["message":body.toString()]
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

log.info "The http server is started"