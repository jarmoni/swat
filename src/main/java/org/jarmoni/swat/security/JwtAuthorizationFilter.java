package org.jarmoni.swat.security;

import static org.jarmoni.swat.security.SecurityConstants.HEADER_STRING;
import static org.jarmoni.swat.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

// Validates token when it has been set as header.
// Unfortunately not useable for websockets, because browsers do not support headers for ws up to now.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	private final JwtValidator jwtValidator;

	public JwtAuthorizationFilter(final AuthenticationManager authManager, final JwtValidator jwtValidator) {

		super(authManager);
		this.jwtValidator = jwtValidator;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain)
			throws IOException, ServletException {

		final String header = req.getHeader(HEADER_STRING);

		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}

		final String token = header.replace(TOKEN_PREFIX, "");

		final UsernamePasswordAuthenticationToken authentication = this.jwtValidator.validateToken(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}
}
