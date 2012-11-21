import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.impl.DefaultHttpServer

/**
 * Web server to handle all incoming web requests.
 *
 * @author Jettro Coenradie
 */

def log = container.logger

RouteMatcher routeMatcher = new RouteMatcher()

routeMatcher.get("/") { req ->
    req.response.sendFile "static/index.html"
}

routeMatcher.get("/favicon.ico") { req ->
    req.response.sendFile "static/img/favicon.ico"
}

routeMatcher.getWithRegEx("^\\/static\\/.*") { req ->
    req.response.sendFile(req.path.substring(1))
}

DefaultHttpServer server = vertx.createHttpServer()
server.requestHandler(routeMatcher.asClosure())

vertx.createSockJSServer(server).bridge(prefix: '/eventbus', [[:]], [[:]])

server.listen(8080)

log.info "The http server is started"