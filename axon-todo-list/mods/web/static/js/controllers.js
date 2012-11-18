'use strict';

angular.module('eventBusServices', []).factory('EventBus', function () {
    return new vertx.EventBus("http://localhost:8080/eventbus");
});

function TodoListCtrl($scope, EventBus) {

    /* Init parameters, some todoItems */
    $scope.todoItems = [];

    $scope.connectionStatus = "Waiting";
    $scope.connectionStatusClass = "alert-error";

    /* Handle the EventBus */
    EventBus.onopen = function () {
        $scope.connectionStatus = "Connected";
        $scope.connectionStatusClass = "alert-success";

        EventBus.registerHandler("message.all.clients", function (msg, replyTo) {
            if (msg.name == "TodoCreated") {
                $scope.todoItems.push({"todoText": msg.todoText, "completed": false, "identifier": msg.identifier});
            } else if (msg.name == "TodoCompleted") {
                var result = $.grep($scope.todoItems, function (e) {
                    return e.identifier == msg.identifier;
                });
                if (result.length == 1) {
                    result[0].completed = true;
                }
            }
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

    $scope.markCompleted = function (todoItem) {
        publish(EventBus, "command.todo.markcompleted", {identifier: todoItem.identifier});
    }
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
