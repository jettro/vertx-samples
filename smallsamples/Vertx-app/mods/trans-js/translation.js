load('vertx.js');

var translations = {
    "en" : "Hello World",
    "nl" : "Hallo Wereld",
    "de" : "Hallo Welt"
};

var eb = vertx.eventBus;

var handler = function (message, replier) {
    replier(translations[message]);
}

var address = "translate.term";
eb.registerHandler(address, handler);

function vertxStop() {
    eb.unregisterHandler(address, handler);
}
