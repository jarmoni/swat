package org.jarmoni.ws_ssh_terminal.ws.message;

public class CommandMessage {
	
	private String line;
	
	public CommandMessage(String line) {
		
		this.line = line;
	}
	
	public String getLine() {
		return line;
	}
}
