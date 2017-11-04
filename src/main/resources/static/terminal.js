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
		$('#messages').append(msg);
	}
	
	function doHttpPost(url, data, successFun) {
		$.ajax({
			type: 'POST',
			url: url,
			contentType: 'application/json',
			data: JSON.stringify(data),
			success: successFun,
			error: function() {
				printToMessageArea('REST-call to=' + url + ' with data=' + data + ' failed');
			}
		});
	}

	var terminal = $('#terminal').terminal(function(command, terminal) {
		sendToWs(JSON.stringify({
			'type': 'command',
			'command': command
		}));
	}, {
		greetings : "Welcome to S.W.A.T.!\nTo start a new SSH-session you have to...\n- Enter adress and credentials ('Settings').\n- Press 'Connect' for launching the Session.\n- Press 'Disonnect' to terminate the session and free all resources.",
		prompt : '$ ',
		exit : false
	});
	window.terminal = terminal;
	
	function createWs() {
		printToMessageArea("Connecting to URL=" + wsUrl);
		ws = new WebSocket(wsUrl);
		
		ws.onopen = function() {
			printToMessageArea('Socket open');
			terminal.freeze(false);
			terminal.enable();
			connected = true;
			var connMsg = JSON.stringify(new SshCredentialsMessage($('#sshServer').val(), $('#sshUser').val(), $('#sshPasswd').val()).create());
			printToMessageArea("Sending connMsg to Ws=" + connMsg);
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
			printToMessageArea("msg=" + msg);
			terminal.echo(msg.data);
		};
	}
	
	function connect() {
		printToMessageArea("Requesting token from URL=" + loginUrl);
		doHttpPost(loginUrl, new WebUser($('#webUser').val(), $('#webPasswd').val()), function(data, status, jqXHR) {
			var token = jqXHR.getResponseHeader('Authorization').replace(TOKEN_PREFIX, "");
			printToMessageArea("Received token=" + token)
		})
		
	}
	
	$('#disconnect').click(function() {
		printToMessageArea("Disonnecting...");
		if(connected) {
			ws.close();
		}
	});
	
	$('#conn-form').submit(function( event ) {
		printToMessageArea("Connecting...");
		event.preventDefault();
		connect();
	});
});
