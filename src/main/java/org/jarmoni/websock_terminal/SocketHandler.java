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
		
		Map<String, String> valueMap = new Gson().fromJson(message.getPayload(), Map.class);
		String type = valueMap.get("type");
		if(type.equals("sshCredentials")) {
			this.initSsh(session, valueMap);
		}
		else if(type.equals("command")) {
			handlers.get(session).write(valueMap.get("command"));
		}
		else {
			LOG.error("Unknown type={}", type);
		}
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		LOG.debug("Host={}, port={}, user={}, passwd={}", this.host, this.port, this.user, this.passwd);
//		this.handlers.put(session, new SshHandler(session, this.host, this.port, this.user, this.passwd));
//		LOG.info("Added new SshHandler for session={}", session != null ? session.getId() : null);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		SshHandler current = this.handlers.get(session);
		if(current != null) {
			this.handlers.get(session).stop();
			this.handlers.remove(session);
			LOG.info("Removed SshHandler for session-id={}", session != null ? session.getId() : null);
		}
		else {
			LOG.info("No SshHandler present for session-id={}", session != null ? session.getId() : null);
		}
	}
	
	private void initSsh(WebSocketSession session, Map<String, String> sshProps) {
		
		try {
			this.handlers.put(session, new SshHandler(session, 
					sshProps.get("server"), Integer.valueOf(sshProps.get("port")), sshProps.get("user"), sshProps.get("passwd")));
			LOG.info("Added new SshHandler for session-id={}", session != null ? session.getId() : null);
		} catch (Exception e) {
			LOG.error("Exception during SSH-initialization", e);
		}
	}

}