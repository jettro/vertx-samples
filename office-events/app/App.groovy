/**
 * @author Jettro Coenradie
 *
 * export VERTX_MODS=/Users/jcoenradie/sources/vertx-samples/office-events/mods
 *
 *
 */
def log = container.logger
def mongoConfig = ["db_name": "officeevents", "address": "vertx.persist"]
container.with {
    deployModule('vertx.mongo-persistor-v1.1', mongoConfig)
    deployModule('website')
    deployModule('notifications')
    deployModule('invitations')
}

