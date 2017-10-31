package org.jarmoni.websock_terminal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfig.class);

	// This is just for test ;-)
	@Value("${ssh.user}")
	private String sshUser;

	@Value("${ssh.passwd}")
	private String sshPasswd;

	@Value("${ssh.host:localhost}")
	private String sshHost;

	@Value("${ssh.port:22}")
	private int sshPort;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(new SocketHandler(this.sshHost, this.sshPort, this.sshUser, this.sshPasswd), "/ws")
				.addInterceptors(new HandshakeInterceptor() {

					@Override
					public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
							WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

						HttpServletRequest origRequest = ((ServletServerHttpRequest) request).getServletRequest();
						
						String user = origRequest.getParameter("webUser");
						String passwd = origRequest.getParameter("webPasswd");
						try {
							SecurityContext context = SecurityContextHolder.createEmptyContext();
							Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, passwd));
							context.setAuthentication(auth);
							SecurityContextHolder.setContext(context);
							return true;
						}
						catch(AuthenticationException e) {
							LOG.warn("Authentication failed for user={}", user);
							return false;
						}

//						/* Retrieve template variables */
//						Map<String, String> uriTemplateVars = (Map<String, String>) origRequest
//								.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//						if (uriTemplateVars != null) {
//							attributes.putAll(uriTemplateVars);
//						}
//						return false;
					}

					@Override
					public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
							WebSocketHandler wsHandler, Exception exception) {
						LOG.debug("After handshake");
						// TODO Auto-generated method stub

					}

				});
	}

}