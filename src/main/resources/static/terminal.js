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
	
	function printToMessageArea(msg) {
		$('#messages').append(msg + "\n");
	}
	
	function doHttpPost(url, data, successFun) {
		$.ajax({
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(data),
			success: successFun,
			error: function() {
				printToMessageArea('REST-call to: ' + url + ' with data: ' + JSON.stringify(data) + ' failed');
			}
		});
	}

	var terminal = $('#terminal').terminal(function(command, terminal) {
		sendToWs(JSON.stringify(new CommandMessage(command).create()));
	}, {
		greetings : WELCOME_MSG,
		prompt : '$ ',
		exit : false
	});
	window.terminal = terminal;
	
	function createWs(token) {
		var fullWsUrl = wsUrl + token;
		printToMessageArea("Connecting to URL=" + fullWsUrl);
		ws = new WebSocket(fullWsUrl);
		
		ws.onopen = function() {
			printToMessageArea('Socket open');
			terminal.freeze(false);
			terminal.enable();
			connected = true;
			var connMsg = JSON.stringify(new SshCredentialsMessage($('#sshServer').val(), $('#sshUser').val(), $('#sshPasswd').val()).create());
			printToMessageArea("Sending connMsg to WS-server: " + connMsg);
			sendToWs(connMsg);
		};
		ws.onclose = function() {
			printToMessageArea('Socket closed');
			connected = false;
			terminal.echo("\n\nWebsocket-connection closed.");
			terminal.disable();
			terminal.freeze(true);
		};
		ws.onmessage = function(msg) {
			console.log("msg=" + msg.data);
			terminal.echo(msg.data);
		};
	}
	
	function connect() {
		printToMessageArea("Requesting token from URL: " + loginUrl);
		doHttpPost(loginUrl, new WebUser($('#webUser').val(), $('#webPasswd').val()), function(data, status, jqXHR) {
			var token = jqXHR.getResponseHeader('Authorization').replace(TOKEN_PREFIX, "");
			printToMessageArea("Received JWT-token: " + token)
			createWs(token);
		})
		
	}
	
	$('#btnDisconnect').click(function() {
		printToMessageArea("Disonnecting...");
		if(connected) {
			ws.close();
		}
	});
	
	$('#btnConnect').click(function() {
		printToMessageArea("Connecting...");
		connect();
	});
});
