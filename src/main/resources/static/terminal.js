$(document).ready(function() {
	var ws = new WebSocket('ws://localhost:8080/ws');

	function send(command) {
		var data = JSON.stringify({
			'command' : command
		})
		ws.send(data);
	}

	function print(data) {
		terminal.echo(data.data);
	}

	var terminal = $('#terminal').terminal(function(command, terminal) {
		send(command);
	}, {
		greetings : 'Welcome to websocket-terminal',
		prompt : '$ ',
		exit : false
	});
	window.terminal = terminal;

	ws.onopen = function() {
		console.log('Socket open');
		terminal.enable();
	};
	ws.onclose = function() {
		console.log('Socket closed');
		terminal.disable();
	};
	ws.onmessage = function(msg) {
		console.log("msg=" + msg);
		print(msg);
	};
});
