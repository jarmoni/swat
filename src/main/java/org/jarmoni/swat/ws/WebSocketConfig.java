package org.jarmoni.swat.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	//	@Autowired
	//	private HandshakeInterceptor interceptor;

	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {

		// this is the 'custom-stuff' we hopefully get rid of...
		//		registry.addHandler(new SocketHandler(), "/ws")
		//				.addInterceptors(interceptor);

		registry.addHandler(new SocketHandler(), "/ws");
	}

}