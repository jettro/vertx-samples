/*
 * Responsible for converting the received Notification json object in string format
 * into an Object. This object is published to all clients. Before publication we
 * add the notification type to it.
 */

load('vertx.js');

var config = vertx.config;
var logger = vertx.logger;

var eventBus = vertx.eventBus;

var handler = function (message) {
    var notification = JSON.parse(message);
    logger.info("Received a notification" + notification.message);
    notification.type = "notification";
    eventBus.publish(config.all_clients_address, notification);
};

eventBus.registerHandler(config.notification_received, handler);

logger.info("The notification module is started.");