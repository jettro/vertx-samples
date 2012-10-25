/*
 * Responsible for converting the received Notification json object in string format
 * into an Object. This object is published to all clients. Before publication we
 * add the notification type to it.
 */

load('vertx.js');

var logger = vertx.logger;

var eventBus = vertx.eventBus;

var handler = function (message) {
    var notification = JSON.parse(message);
    logger.info("Received a notification" + notification.message);
    notification.type = "notification";
    eventBus.publish("message.all.clients", notification);
};

eventBus.registerHandler("notification.received", handler);

logger.info("Started the notification module.");