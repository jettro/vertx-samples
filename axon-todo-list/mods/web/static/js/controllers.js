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

        var mongoAction = {"action": "find", "collection": "todos", "matcher": {}};
        EventBus.send("vertx.mongo.persist", mongoAction, function (reply) {
            var msg = reply.results;
            for (var i = 0; i < msg.length; i++) {
                var todoItem = msg[i];
                $scope.todoItems.push({"todoText": todoItem.todoText, "completed": todoItem.completed, "identifier": todoItem.identifier});
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
        send(EventBus, "command.todo.create", {todoText: $scope.todoText});
        $scope.todoText = '';
    };

    $scope.markCompleted = function (todoItem) {
        send(EventBus, "command.todo.markcompleted", {identifier: todoItem.identifier});
    }
}

function send(eventbus, address, message) {
    eventbus.send(address, message, function (reply) {
        /* Hiding an element is still hard with angularjs, so for now we do it the old fashioned way */
        var replyMessageDiv = $('#replymessage');
        replyMessageDiv.html("<div class=\"alert alert-info\">" + reply.message + "</div>");
        replyMessageDiv.fadeIn('fast');
        replyMessageDiv.fadeOut(5000);
    });
}
