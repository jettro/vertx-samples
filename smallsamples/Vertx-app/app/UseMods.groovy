import org.vertx.groovy.core.eventbus.EventBus

/**
 * @author Jettro Coenradie
 *
 * export VERTX_MODS=/Users/jcoenradie/sources/vertx-samples/smallsamples/Vertx-app/mods/
 */

container.deployVerticle('trans-js')

EventBus eb = vertx.eventBus

vertx.createHttpServer().requestHandler { req ->
    def language = req.params["lang"] ?: "en"
    eb.send("translate.term", language) {response ->
        req.response.end response.body
    }
}.listen(8080)