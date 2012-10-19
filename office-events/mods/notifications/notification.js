load('vertx.js');

var logger = vertx.logger;

var eb = vertx.eventBus;

var senderAddress = "message.send.notification";

var handler = function (message) {
    var parse = JSON.parse(message);
    logger.info("Received a message from" + parse.message);
    eb.send(senderAddress, parse.message);
};

var address = "notification.received";
eb.registerHandler(address, handler);

function vertxStop() {
    eb.unregisterHandler(address, handler);
}

logger.info("Started the notification module listening to " + address);