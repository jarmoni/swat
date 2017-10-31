package org.jarmoni.websock_terminal;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

//SocketHandler is created by 'WebSocketConfig' so it is NOT managed by Spring (@Component)
public class SocketHandler extends TextWebSocketHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);
	
	private String host, user, passwd;
	private int port;
	
	private Map<WebSocketSession, SshHandler> handlers = new ConcurrentHashMap<>();
	
	public SocketHandler(String host, int port, String user, String passwd) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.passwd = passwd;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		
		Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);
		String command = value.get("command");
		handlers.get(session).write(command);
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		LOG.debug("Host={}, port={}, user={}, passwd={}", this.host, this.port, this.user, this.passwd);
		this.handlers.put(session, new SshHandler(session, this.host, this.port, this.user, this.passwd));
		LOG.info("Added new SshHandler for session={}", session != null ? session.getId() : null);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		this.handlers.get(session).stop();
		this.handlers.remove(session);
		LOG.info("Removed SshHandler for session={}", session != null ? session.getId() : null);
	}

}