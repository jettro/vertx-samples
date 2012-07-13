import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.core.http.impl.DefaultHttpServer
import org.vertx.groovy.core.buffer.Buffer
/**
 * @author Jettro Coenradie
 */

RouteMatcher routeMatcher = new RouteMatcher()

routeMatcher.get("/") {req ->
    req.response.sendFile "static/index2.html"
}

def header = new File("tpl/header.tpl").getText()
def footer = new File("tpl/footer.tpl").getText()
routeMatcher.get("/greeting/:message") {req ->
    def greeting = req.params["message"]

    def buffer = new Buffer()
    buffer.appendString(header)
    buffer.appendString(greeting)
    buffer.appendString(footer)
    req.response.end buffer
}

routeMatcher.getWithRegEx("^\\/static\\/.*") { req ->
    req.response.sendFile(req.path.substring(1))
}

DefaultHttpServer server = vertx.createHttpServer()
server.requestHandler (routeMatcher.asClosure()).listen(8080)