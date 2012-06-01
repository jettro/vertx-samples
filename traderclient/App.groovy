package traderclient

import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.groovy.core.buffer.Buffer

/**
 * @author Jettro Coenradie
 */
EventBus eventBus = vertx.eventBus
def server = vertx.createHttpServer()
//def sockJSBridgeConfig = ["prefix":"/eventbus"]
vertx.createSockJSServer(server).bridge(prefix:'/eventbus',[[:]])

def routeMatcher = new RouteMatcher()

routeMatcher.get("/trades") {req ->
    def query = ["action":"find","collection":"tradeExecutedEntry","matcher":[:]]
    eventBus.send("vertx.mongopersistor",query) {message ->
        def buffer = new Buffer();
        buffer.appendBytes(new File('traderclient/header.tpl').readBytes())

        buffer.appendString("<table class='table table-striped'><thead><tr><th>Company</th><th>amount</th><th>prize</th></tr></thead><tbody>")
        def results = message.body.results
        results.each { item ->
            buffer.appendString("<tr><td>${item.companyName}</td><td>${item.tradeCount}</td><td>${item.tradePrice}</td></tr>")
        }
        buffer.appendString("</tbody></table>")

        buffer.appendBytes(new File('traderclient/footer.tpl').readBytes())
        req.response.end buffer
    }
}

routeMatcher.get("/orders/:type") {req ->
    def type = req.params["type"]
    if (type == "sell")
        req.response.end "You requested buy orders"
    else if (type == "buy")
        req.response.end "You requested sell orders"
    else
        req.response.end "You requested orders of unknown type"
}

routeMatcher.get("/") { req ->
    req.response.sendFile("traderclient/static/index.html")
}

routeMatcher.getWithRegEx("^\\/static\\/.*") { req ->
    req.response.sendFile("traderclient/" + req.path.substring(1))
}

//routeMatcher.noMatch{ req ->
//    req.response.end "Sorry I cannot help you"
//}

server.requestHandler(routeMatcher.asClosure()).listen(8080, "localhost")

def mongoConfig = ["db_name":"axontrader"]

container.with {
    deployVerticle('mongo-persistor', mongoConfig)
}