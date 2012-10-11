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

eb.registerHandler("NUEVENNIET.message.registerinvitation", function (message) {
    logger.info(message.invitationid);
    var json = {
        "action":"update",
        "collection":"invites",
        "criteria":{
            "_id":message.invitationid
        },
        "objNew":{
            "$inc":{
                "registeredPersons":1
            }
        },
        "upsert":true,
        "multi":false
    };
    logger.info(JSON.stringify(json));
    eb.send("vertx.persist", json, function (message) {
        var parse = JSON.stringify(message);
        logger.info("Received a message from: " + parse);
    });
});

function vertxStop() {
    eb.unregisterHandler(address, handler);
}
