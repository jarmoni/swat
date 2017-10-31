$(document).ready(function() {
	var host = 'localhost:8080';
	//var wsUrl = 'ws://' + host + '/ws';
	//var wsUrl = 'ws://user:password@' + host + '/ws';
	var connParams = jQuery.param({
		webUser: 'user',
		webPasswd: 'password'
	});
	var wsUrl = 'ws://' + host + '/ws?' + connParams;
	var connectUrl = 'http://' + host + '/connect';
	var disconnectUrl = 'http://' + host + '/disconnect';
	
	var connected = false;
	var ws;
	

	function sendToWs(command) {
		var data = JSON.stringify({
			'command' : command
		})
		if(connected) {
			ws.send(data);
		}
	}
	
	function doHttpPost(url, data, successFun) {
		$.ajax({
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(data),
			success: successFun,
			error: function() {
				console.log('REST-call to=' + url + ' with data=' + data + ' failed');
			}
		});
	}

	var terminal = $('#terminal').terminal(function(command, terminal) {
		sendToWs(command);
	}, {
		greetings : 'Welcome to websocket-terminal',
		prompt : '$ ',
		exit : false
	});
	window.terminal = terminal;
	
	function connect() {
		ws = new WebSocket(wsUrl);
		
		ws.onopen = function() {
			console.log('Socket open');
			terminal.freeze(false);
			terminal.enable();
		};
		ws.onclose = function() {
			console.log('Socket closed');
			terminal.echo("\n\nWebsocket-connection closed.");
			terminal.disable();
			terminal.freeze(true);
		};
		ws.onmessage = function(msg) {
			console.log("msg=" + msg);
			terminal.echo(msg.data);
		};
		connected = true;
	}
	
	$('#disconnect').click(function() {
		console.log("Disonnecting...");
		if(connected) {
			ws.close();
		}
	});
	
	$('#conn-form').submit(function( event ) {
		console.log("Connecting...");
//		var reqBody = {};
//		reqBody['user'] = $('#user').val();
//		reqBody['passwd'] = $('#passwd').val();
//		reqBody['server'] = $('#server').val();
//		reqBody['port'] = $('#port').val();
//		console.log(JSON.stringify(reqBody));
//		//var $inputs = $('#conn-form :input');
//		doHttpPost(connectUrl, reqBody, function(response) {
//			connect();
//			console.log("Connected...");
//		});
		connect();
		console.log("Connected...");
		event.preventDefault();
	});

	
});
