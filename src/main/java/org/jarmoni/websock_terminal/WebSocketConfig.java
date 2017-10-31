package org.jarmoni.websock_terminal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	// This is just for test ;-)
	@Value("${ssh.user}")
	private String sshUser;

	@Value("${ssh.passwd}")
	private String sshPasswd;

	@Value("${ssh.host:localhost}")
	private String sshHost;

	@Value("${ssh.port:22}")
	private int sshPort;

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry.addHandler(new SocketHandler(this.sshHost, this.sshPort, this.sshUser, this.sshPasswd), "/ws");
	}

}