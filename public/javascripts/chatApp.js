angular.module('chatApp', [])
    .controller('ChatCtrl', ['$scope', function($scope) {

        var wsUri = "ws://"+window.location.host+"/ws";
        var websocket = new WebSocket(wsUri);


        $scope.name = "";
        $scope.messages = [];
        $scope.registered = false;
        $scope.taken = false;
        $scope.sendMessage = function () {
            websocket.send(angular.toJson({
                "messageType": 1,
                "messageText":$scope.messageText
            }));
            $scope.messageText = "";
        };
        $scope.sendName = function () {
            websocket.send(angular.toJson({
                "messageType": 0,
                "messageText":$scope.name
            }));
        };

        websocket.onmessage = function (e) {
            var msg = angular.fromJson(e.data);
            console.log(e.data);
            switch (msg.from) {
                case "system":
                    handleSystemMsg(msg.messageText);
                    break;
            }
            $scope.messages.push(msg);
            $scope.$apply();
            var chatWindow = $("#chat-window");
            chatWindow.scrollTop(chatWindow[0].scrollHeight);
        };

        function handleSystemMsg(msg) {
            switch (msg) {
                case "welcome":
                    $scope.registered = true;
                    break;
                case "taken":
                    $scope.taken = true;
                    break;
            }
        }
    }]);