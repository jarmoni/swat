package org.jarmoni.ws_ssh_terminal.ws.message;

public class SshCredentialsMessage  {
	
	private String sshServer;
	private int sshPort;
	private String sshUser;
	private String sshPasswd;
	
	public SshCredentialsMessage(String sshServer, int sshPort, String sshUser, String sshPasswd) {
		
		this.sshServer = sshServer;
		this.sshPort = sshPort;
		this.sshUser = sshUser;
		this.sshPasswd = sshPasswd;
	}

	public String getSshServer() {
		return sshServer;
	}

	public int getSshPort() {
		return sshPort;
	}

	public String getSshUser() {
		return sshUser;
	}

	public String getSshPasswd() {
		return sshPasswd;
	}
}
