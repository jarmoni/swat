
var ws;

$(function() {
    $('body').terminal(function(command, term) {
        sendCommand(command)
    }, {
        greetings: 'Simple php example',
        onBlur: function() {
            return false;
        }
    });
});

connect();

function setConnected(connected) {
	$('body').terminal({
		welcome: 'Connected!\n'
	})
}

function connect() {
	ws = new WebSocket('ws://localhost:8080/name');
	ws.onmessage = function(data){
		showGreeting(data.data);
	}
	 setConnected(true);
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendCommand(command) {
	var data = JSON.stringify({'command': command})
    ws.send(data);
}

function showGreeting(message) {
	console.log("greetings=" + $("#greetings").val() + ", message=" + message)
    $("#greetings").append(message);
    //scrollToBottom()
}

function scrollToBottom() {
	  $('#greetings').scrollTop($('#greetings')[0].scrollHeight);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

