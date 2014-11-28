var wsUri = "ws://localhost:9000/ws";

websocket = new WebSocket(wsUri);

function ChatCtrl($scope) {
    $scope.messages = [];
    $scope.sendMessage = function () {
        websocket.send($scope.messageText);
        $scope.messageText = "";
    };

    websocket.onmessage = function (e) {
        $scope.messages.push(e.data);
        $scope.$apply();
    };
}