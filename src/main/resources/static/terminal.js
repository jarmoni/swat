$(document).ready(function() {
	var host = 'localhost:8080';
	//var wsUrl = 'ws://' + host + '/ws';
	//var wsUrl = 'ws://user:password@' + host + '/ws';
	var connParams = jQuery.param({
		webUser: 'user',
		webPasswd: 'password'
	});
	var wsUrl = 'ws://' + host + '/ws?' + connParams;
//	var connectUrl = 'http://' + host + '/connect';
//	var disconnectUrl = 'http://' + host + '/disconnect';
	
	var connected = false;
	var ws;
	

	function sendToWs(data) {
		if(connected) {
			ws.send(data);
		}
		else {
			alert('No websocket-connection present')
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
		sendToWs(JSON.stringify({
			'type': 'command',
			'command': command
		}));
	}, {
		greetings : 'Welcome to websocket-terminal',
		prompt : '$ ',
		exit : false
	});
	window.terminal = terminal;
	
	function connect() {
		console.log("Connecting to URL=" + wsUrl);
		ws = new WebSocket(wsUrl);
		
		ws.onopen = function() {
			console.log('Socket open');
			terminal.freeze(false);
			terminal.enable();
			connected = true;
			var connMsg = JSON.stringify({
				'type': 'sshCredentials',
				'server': $('#server').val(),
				'port': $('#port').val(),
				'user': $('#user').val(),
				'passwd': $('#passwd').val()
			});
			console.log("Sending connMsg to Ws=" + connMsg);
			sendToWs(connMsg);
		};
		ws.onclose = function() {
			console.log('Socket closed');
			connected = false;
			terminal.echo("\n\nWebsocket-connection closed.");
			terminal.disable();
			terminal.freeze(true);
		};
		ws.onmessage = function(msg) {
			console.log("msg=" + msg);
			terminal.echo(msg.data);
		};
	}
	
	$('#disconnect').click(function() {
		console.log("Disonnecting...");
		if(connected) {
			ws.close();
		}
	});
	
	$('#conn-form').submit(function( event ) {
		console.log("Connecting...");
		event.preventDefault();
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
	});

	
});
