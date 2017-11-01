package org.jarmoni.ws_ssh_terminal.ws;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jarmoni.ws_ssh_terminal.ssh.SshHandler;
import org.jarmoni.ws_ssh_terminal.ws.message.SshCredentialsMessage;
import org.jarmoni.ws_ssh_terminal.ws.message.WsMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

//SocketHandler is created by 'WebSocketConfig' so it is NOT managed by Spring (@Component)
// TODO: Move the whole message-dispatching to different class
public class SocketHandler extends TextWebSocketHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);

	private final Gson gson = new Gson();

	private final Map<WebSocketSession, SshHandler> handlers = new ConcurrentHashMap<>();

	@Override
	public void handleTextMessage(final WebSocketSession session, final TextMessage message)
			throws InterruptedException, IOException {

		final WsMessageWrapper wrapper = this.gson.fromJson(message.getPayload(), WsMessageWrapper.class);

		if (wrapper.ping != null) {
			// send ping back to origin
			session.sendMessage(new TextMessage(this.gson.toJson(wrapper.ping)));
		} else if (wrapper.sshCredentials != null) {
			// create ssh-handler for this session
			// TODO if handler already exists
			this.initSsh(session, wrapper.sshCredentials);
		} else if(wrapper.command != null) {
			// write to input-stream of SSH-session
			handlers.get(session).write(wrapper.command.getLine());
		} else {
			LOG.error("Unknown message-type={}", message.getPayload());
		}
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession session) throws Exception {

		LOG.debug("Connection established for session-id={}", session != null ? session.getId() : null);
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {

		final SshHandler current = this.handlers.get(session);
		if (current != null) {
			this.handlers.get(session).stop();
			this.handlers.remove(session);
			LOG.info("Removed SshHandler for session-id={}", session != null ? session.getId() : null);
		} else {
			LOG.info("No SshHandler present for session-id={}", session != null ? session.getId() : null);
		}
	}

	private void initSsh(final WebSocketSession session, final SshCredentialsMessage sshCredentials) {

		try {
			this.handlers.put(session, new SshHandler(session, sshCredentials.getSshServer(),
					sshCredentials.getSshPort(), sshCredentials.getSshUser(), sshCredentials.getSshPasswd()));
			LOG.info("Added new SshHandler for session-id={}", session != null ? session.getId() : null);
		} catch (final Exception e) {
			LOG.error("Exception during SSH-initialization", e);
		}
	}

}