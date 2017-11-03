package org.jarmoni.ws_ssh_terminal.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

// Spring-security does not support websockets without STOMP (https://github.com/spring-projects/spring-security/issues/3915)
// This is a workaround for that. Can be removed if Spring-security will handle plain-ws in future
// @Service
public class WsSecurityHandshakeInterceptor implements HandshakeInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(WsSecurityHandshakeInterceptor.class);

	//@Autowired
	private final AuthenticationManager authenticationManager;

	private final JwtValidator jwtValidator;

	public WsSecurityHandshakeInterceptor(final AuthenticationManager authenticationManager, final JwtValidator jwtValidator) {

		this.authenticationManager = authenticationManager;
		this.jwtValidator = jwtValidator;
	}

	@Override
	public boolean beforeHandshake(final ServerHttpRequest request, final ServerHttpResponse response,
			final WebSocketHandler wsHandler, final Map<String, Object> attributes) throws Exception {

		final HttpServletRequest origRequest = ((ServletServerHttpRequest) request).getServletRequest();

		final String token = origRequest.getParameter("token");
		try {
			final SecurityContext context = SecurityContextHolder.createEmptyContext();
			final Authentication auth = authenticationManager.authenticate(jwtValidator.validateToken(token));
			context.setAuthentication(auth);
			SecurityContextHolder.setContext(context);
			return true;
		}
		catch(final AuthenticationException e) {
			LOG.warn("Authentication failed for token={}", token);
			return false;
		}
	}

	@Override
	public void afterHandshake(final ServerHttpRequest request, final ServerHttpResponse response,
			final WebSocketHandler wsHandler, final Exception exception) {

		LOG.debug("After handshake");
	}


}
