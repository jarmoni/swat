package org.jarmoni.websock_terminal;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

@Component
public class SocketHandler extends TextWebSocketHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);
	
	// This is just for test ;-)
	@Value("${ssh.user}")
	private String sshUser;
	
	@Value("${ssh.passwd}")
	private String sshPasswd;
	
	@Value("${ssh.host:localhost}")
	private String sshHost;
	
	@Value("${ssh.port:22}")
	private int sshPort;
	
	private Map<WebSocketSession, SshHandler> handlers = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		
		Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);
		String command = value.get("command");
		LOG.debug("From Terminal-input={}", command);
		handlers.get(session).write(command);
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		this.handlers.put(session, new SshHandler(session, this.sshHost, this.sshPort, this.sshUser, this.sshPasswd));
		LOG.info("Added new SshHandler for session={}", session != null ? session.getId() : null);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		this.handlers.get(session).stop();
		this.handlers.remove(session);
		LOG.info("Removed SshHandler for session={}", session != null ? session.getId() : null);
	}

}