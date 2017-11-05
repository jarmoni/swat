package org.jarmoni.swat.ws.message;

public class SshCredentialsMessage  {
	
	private String sshHost;
	private int sshPort;
	private String sshUser;
	private String sshPasswd;
	
	public SshCredentialsMessage(String sshHost, int sshPort, String sshUser, String sshPasswd) {
		
		this.sshHost = sshHost;
		this.sshPort = sshPort;
		this.sshUser = sshUser;
		this.sshPasswd = sshPasswd;
	}

	public String getSshHost() {
		return sshHost;
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
