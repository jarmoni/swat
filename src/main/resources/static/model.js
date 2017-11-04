// WS-messages: Use create()-method

class PingMessage {
	constructor(uuid) {
		this.uuid = uuid;
	}
	create() {
		return { ping: this };
	}
}


class CommandMessage {
	constructor(line) {
		this.line = line;
	}
	create() {
		return { command: this};
	}
}

class SshCredentialsMessage {
	constructor(sshServer, sshUser, sshPasswd) {
		this.sshHost = sshServer.split(':')[0];
		this.sshPort = sshPort.split(':')[1];
		this.sshUser = sshUser;
		this.sshPasswd = sshPasswd;
	}
	create() {
		return { sshCredentials: this };
	}
}

// REST-call
class WebUser {
	constructor(webUsername, webPasswd) {
		this.webUsername = webUsername;
		this.webPasswd = webPasswd;
	}
}