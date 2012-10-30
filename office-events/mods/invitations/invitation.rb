# This is the invitation module. The module contains two handlers, one
# for creating a new invitation and one to accept an invitation.
#
# Messages are send to the vertx.persist address that shouls somehow
# store the invitations and the registrations

require "vertx"
include Vertx

config = Vertx.config
logger = Vertx.logger

logger.info config["all_clients_address"]

EventBus.register_handler(config["invitation_new"]) do |message|
    message.reply({'message'=>'Thank you for the invitation'})
    receivedMessage = message.body['message']
    receivedMaxPersons = message.body['maxpersons']

    EventBus.send(config["vertx_persist"],
        {
            'action' => 'save',
            'collection' => 'invites',
            'document' => {
                'message' => receivedMessage,
                'maxPersons' => receivedMaxPersons,
                'registeredPersons' => 0
            }
        }) do |replyMessage|
            invitation = {
                'id'=>replyMessage.body['_id'],
                'message'=>receivedMessage,
                'maxPersons'=>receivedMaxPersons,
                'type'=>'invitation'
            }
            EventBus.publish(config["all_clients_address"],invitation)
    end
end

EventBus.register_handler(config["invitation_register"]) do |message|
    message.reply({'message'=>'You are now welcome to join me'})

    EventBus.send(config["vertx_persist"],
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

logger.info "The invitation module is started"