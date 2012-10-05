/**
 * @author Jettro Coenradie
 *
 * export VERTX_MODS=/Users/jcoenradie/sources/vertx-samples/office-events/mods
 *
 *
 */

container.with {
    deployModule('website')
    deployModule('notifications')
    deployModule('invitations')
}

