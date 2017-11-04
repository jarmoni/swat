$(document).ready(function() {
	
	var loginUrl = ($('#webTls').is(':checked') ? 'https' : 'http') + '://' + $('#webServer').val() + LOGIN_URL_POSTFIX;
	var wsUrl = ($('#webTls').is(':checked') ? 'wss' : 'ws') + '://' + $('#webServer').val() + WS_URL_POSTFIX;
	
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
	
	function createWs() {
		console.log("Connecting to URL=" + wsUrl);
		ws = new WebSocket(wsUrl);
		
		ws.onopen = function() {
			console.log('Socket open');
			terminal.freeze(false);
			terminal.enable();
			connected = true;
			var connMsg = JSON.stringify(new SshCredentialsMessage($('#sshServer').val(), $('#sshUser').val(), $('#sshPasswd').val()).create());
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
	
	function connect() {
		console.log("Requesting token from URL=" + loginUrl);
		doHttpPost(loginUrl, new WebUser($('#webUser').val(), $('#webPasswd').val()), function(data, status, jqXHR) {
			var token = jqXHR.getResponseHeader('Authorization').replace(TOKEN_PREFIX, "");
			console.log("Received token=" + token)
		})
		
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
		connect();
	});
});
