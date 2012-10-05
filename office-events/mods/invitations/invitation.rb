require "vertx"
include Vertx

logger = Vertx.logger

EventBus.register_handler("message.newinvitation") do |message|
    theMessage = message.body['message'] + " (max " + message.body['maxpersons'] + " personen)"
    logger.info theMessage
    EventBus.send("message.send.invitation",theMessage)
end