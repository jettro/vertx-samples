'use strict';

angular.module('eventBusServices', []).factory('EventBus', function () {
    return new vertx.EventBus("http://localhost:8080/eventbus");
});

function TodoListCtrl($scope, EventBus) {

    /* Init parameters */
    $scope.todoItems = [
        {"todoText": "Implement persistence",
            "completed": false},
        {"todoText": "Make use of AngularJS",
            "completed": false},
        {"todoText": "Have fun",
            "completed": true}
    ];

    $scope.connectionStatus = "Waiting";
    $scope.connectionStatusClass = "alert-error";

    /* Handle the EventBus */
    EventBus.onopen = function () {
        $scope.connectionStatus = "Connected";
        $scope.connectionStatusClass = "alert-success";

        EventBus.registerHandler("message.all.clients", function (msg, replyTo) {
            $scope.todoItems.push({"todoText": msg.todoText, "completed": false});
            $scope.$digest();
        });
        $scope.$digest();
    };

    EventBus.onclose = function () {
        $scope.connectionStatus = "Not connected";
        $scope.connectionStatusClass = "alert-error";
        $scope.$digest();
    };

    /* Controller methods */
    $scope.addTodo = function () {
        publish(EventBus, "command.todo.create", {todoText: $scope.todoText});
        $scope.todoText = '';
    };

}

function publish(eventbus, address, message) {
    eventbus.send(address, message, function (reply) {
        /* Hiding an element is still hard with angularjs, so for now we do it the old fashioned way */
        var replyMessageDiv = $('#replymessage');
        replyMessageDiv.html("<div class=\"alert alert-info\">" + reply.message + "</div>");
        replyMessageDiv.fadeIn('fast');
        replyMessageDiv.fadeOut(5000);
    });
}
