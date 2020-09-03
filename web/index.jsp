<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ChatRoom</title>
    </head>
    <body>
        <h1>MyChatRoom</h1>
        <div id="msgBody">
            <div id="msgDiv">
                <p id="statusMsg">Connect to ChatRoom...</p>
            </div>
        </div>
        <div id="onlineBody">
            <div id="onlineDiv">
                Online:
            </div>
        </div>
        <div id="sendDiv">
            <p>Color:</p>
            <button id="colorField" class="jscolor {valueElement:null, value:'000000'}"></button>
            <p>Message:</p>
            <input style="width:270px" type="text" id="msgField" name="msg" value="" autocomplete="off" required disabled/>
            <input type="button" id="sendBtn" value="Send" disabled/>
        </div>
    </body>
    <link type="text/css" rel="stylesheet" href="<%=application.getContextPath()%>/css/main.css"/>
    <script src="<%=application.getContextPath()%>/js/jquery-1.12.1.min.js" type="text/javascript"></script>
    <script src="<%=application.getContextPath()%>/js/jscolor.min.js" type="text/javascript"></script>
    <script>
        var websocket;
        var key;
        var sender = null;

        $(document).ready(init);
        function init() {
            setKeyboard();
            getNickname();
            getWebsocket();
            getSessionId();
        }

        function setKeyboard() {
            $(window).keydown(function (event) {
                if (event.keyCode === 13) {
                    $('#sendBtn').click();
                }
            });
        }

        function getNickname() {
            var input = prompt("Enter your nickname:");
            if (input === null || input === '') {
                getNickname();
            } else {
                sender = input;
            }
        }

        function getWebsocket() {
            var wsUri = "ws://localhost:8080/ChatRoom/ws/" + sender;
            websocket = new WebSocket(wsUri);
            websocket.onmessage = function (evt) {
                receiveHandler(evt.data);
            };
            websocket.onerror = function (evt) {
                console.log(evt);
            };
        }
        function receiveHandler(json) {
            var rs = JSON.parse(json);
            if (rs.op === "getSessionId") {
                key = rs['key'];
            } else if (rs.op === "getMsgs") {
                rs.data.forEach(function (msgBean) {
                    $('#msgDiv').append('<p style="color:' + msgBean['color'] + '">' + msgBean['sender'] + " : " + msgBean['msg'] + '</p>');
                });
                $("#msgBody").scrollTop($("#msgBody")[0].scrollHeight);
            } else if (rs.op === "getOnlineList") {
                $('#onlineDiv').text('').append('Online:');
                rs.data.forEach(function (userBean) {
                    $('#onlineDiv').append('<p>' + '['+ userBean['id'] + ']-' + userBean['nickName'] + '</p>');
                });
                $("#msgBody").scrollTop($("#msgBody")[0].scrollHeight);
            }
        }

        function getSessionId() {
            setTimeout(function () {
                if (websocket.readyState === 1) {
                    $('#statusMsg').append('OK!!');
                    $('#msgDiv').append('<p>Welcome to ChatRoom!</p>');

                    $('#msgField').removeAttr('disabled');
                    $('#sendBtn').removeAttr('disabled');

                    var rq = {op: 'getSessionId'};
                    websocket.send(JSON.stringify(rq));
                } else {
                    getSessionId();
                }
            }, 100);
        }

        $('#sendBtn').click("click", sendHandler);
        function sendHandler() {
            if ($('#msgField').val() === "" || $('#senderField').val() === "") {
                return;
            }

            $.ajax({
                method: "POST",
                data: {op: 'uploadMsg', key: key, sender: sender, msg: $('#msgField').val(), color: $('#colorField').css('background-color')},
                url: "<%=application.getContextPath()%>/msg.do"
            }).done(sendDoneHandler);
        }
        function sendDoneHandler() {
            $('#msgDiv').append('<p style="color:' + $('#colorField').css('background-color') + '">' + sender + " : " + $('#msgField').val() + '</p>');
            $('#msgField').val("");
            $("#msgBody").scrollTop($("#msgBody")[0].scrollHeight);
            $('#msgField').focus();
            pushMsgs();
        }
        function pushMsgs() {
            var rq = {op: 'pushMsgs'};
            websocket.send(JSON.stringify(rq));
        }
    </script>
</html>
