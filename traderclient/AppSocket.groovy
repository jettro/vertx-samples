package traderclient

import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.groovy.core.http.RouteMatcher

def server = vertx.createHttpServer()
def routeMatcher = new RouteMatcher()

routeMatcher.get("/") { req ->
    req.response.sendFile("traderclient/static/socketDemo.html")
}

routeMatcher.getWithRegEx("^\\/static\\/.*") { req ->
    req.response.sendFile("traderclient/" + req.path.substring(1))
}

server.requestHandler(routeMatcher.asClosure())

vertx.createSockJSServer(server).bridge(prefix: '/eventbus', [[:]])

server.listen(9090)

EventBus eb = vertx.eventBus
vertx.setPeriodic(1000l) {
    def query = ["action": "find", "collection": "tradeExecutedEntry", "matcher": [:]]

    eb.send("vertx.mongopersistor", query) {message ->
        eb.send("updates.trades", message.body)
    }
}

def mongoConfig = ["db_name": "axontrader"]
container.with {
    deployVerticle('mongo-persistor', mongoConfig)
}