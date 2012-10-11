require "vertx"
include Vertx

logger = Vertx.logger

EventBus.register_handler("message.newinvitation") do |message|
    message.reply({'message'=>'Thank you for the invitation'})
    receivedMessage = message.body['message']
    receivedMaxPersons = message.body['maxpersons']

    EventBus.send("vertx.persist",
        {
            'action' => 'save',
            'collection' => 'invites',
            'document' => {
                'message' => receivedMessage,
                'maxPersons' => receivedMaxPersons,
                'registeredPersons' => 0
            }
        }) do |replyMessage|
        theMessage = {'id'=>replyMessage.body['_id'],'message'=>receivedMessage,'maxPersons'=>receivedMaxPersons}
        EventBus.send("message.send.invitation",theMessage)
    end
end

EventBus.register_handler("message.registerinvitation") do |message|
    message.reply({'message'=>'You are now welcome to join me'})

    EventBus.send("vertx.persist",
        {
            'action' => 'update',
            'collection' => 'invites',
            'criteria' => {
                '_id' => message.body['invitationid']
            },
            'objNew' => {
                '$inc' => {
                    'registeredPersons' => 1
                }
            },
            'upsert' => true,
            'multi' => false
         }) do |replyMessage|
        logger.info "We stored the updated number of registered persons for " + message.body['invitationid']
    end

end
