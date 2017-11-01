package org.jarmoni.ws_ssh_terminal.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Service
public class WsSecurityHandshakeInterceptor implements HandshakeInterceptor {
	
	private static final Logger LOG = LoggerFactory.getLogger(WsSecurityHandshakeInterceptor.class);
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	private JwtValidator jwtValidator;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

		HttpServletRequest origRequest = ((ServletServerHttpRequest) request).getServletRequest();
		
		String token = origRequest.getParameter("token");
		try {
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			Authentication auth = authenticationManager.authenticate(jwtValidator.validateToken(token));
			context.setAuthentication(auth);
			SecurityContextHolder.setContext(context);
			return true;
		}
		catch(AuthenticationException e) {
			LOG.warn("Authentication failed for token={}", token);
			return false;
		}
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Exception exception) {
		
		LOG.debug("After handshake");
	}


}
