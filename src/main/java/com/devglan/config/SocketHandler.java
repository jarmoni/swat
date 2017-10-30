package com.devglan.config;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

@Component
public class SocketHandler extends TextWebSocketHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);
	
	private Map<WebSocketSession, SshHandler> handlers = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		System.out.println("MSG=" + message);
		Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);
		String command = value.get("command");
		LOG.debug("Command={}", command);
		handlers.get(session).write(command);
//		System.out.println("value=" + message);
//		/*for(WebSocketSession webSocketSession : sessions) {
//			webSocketSession.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
//		}*/
//			session.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		this.handlers.put(session, new SshHandler(session));
		LOG.info("Added new SshHandler for session={}", session != null ? session.getId() : null);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		this.handlers.remove(session);
		LOG.info("Removed SshHandler for session={}", session != null ? session.getId() : null);
	}

}