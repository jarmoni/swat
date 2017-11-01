package org.jarmoni.ws_ssh_terminal.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	@Autowired
	private HandshakeInterceptor interceptor;

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(new SocketHandler(), "/ws")
				.addInterceptors(interceptor);
	}

}