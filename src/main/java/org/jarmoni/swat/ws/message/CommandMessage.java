package org.jarmoni.swat.ws.message;

public class CommandMessage {
	
	private String line;
	
	public CommandMessage(String line) {
		
		this.line = line;
	}
	
	public String getLine() {
		return line;
	}
}
