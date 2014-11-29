var wsUri = "ws://localhost:9000/ws";

websocket = new WebSocket(wsUri);

function ChatCtrl($scope) {
    $scope.messages = [];
    $scope.sendMessage = function () {
        websocket.send(angular.toJson({
            "messageText":$scope.messageText
        }));
        $scope.messageText = "";
    };

    websocket.onmessage = function (e) {
        var message = angular.fromJson(e.data);
        $scope.messages.push(message);
        $scope.$apply();
        var chatWindow = $("#chat-window")
        chatWindow.scrollTop(chatWindow[0].scrollHeight);
    };
}