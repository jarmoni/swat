package org.jarmoni.swat.ws.message;

public class PingMessage {

	private final String uuid;

	public PingMessage(final String uuid) {

		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}
}
