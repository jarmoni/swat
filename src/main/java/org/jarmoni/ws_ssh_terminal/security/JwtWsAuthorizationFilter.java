package org.jarmoni.ws_ssh_terminal.security;

import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.HEADER_STRING;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtWsAuthorizationFilter extends BasicAuthenticationFilter {

	private final JwtValidator jwtValidator;

	public JwtWsAuthorizationFilter(final AuthenticationManager authManager, final JwtValidator jwtValidator) {

		super(authManager);
		this.jwtValidator = jwtValidator;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain)
			throws IOException, ServletException {

		final String token = req.getParameter(HEADER_STRING);

		if(token == null) {
			chain.doFilter(req, res);
		}

		final UsernamePasswordAuthenticationToken authentication = this.jwtValidator.validateToken(token);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}
}
