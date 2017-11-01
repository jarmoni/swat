package org.jarmoni.ws_ssh_terminal.security;

import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.EXPIRATION_TIME;
import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.HEADER_STRING;
import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.SECRET;
import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jarmoni.ws_ssh_terminal.user.WebUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.gson.Gson;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


	private AuthenticationManager authenticationManager;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		
		this.authenticationManager = authenticationManager;
		this.setUsernameParameter(WebUser.PROP_USERNAME);
		this.setPasswordParameter(WebUser.PROP_PASSWD);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		
		try {
			WebUser webUser = new Gson().fromJson(new InputStreamReader(req.getInputStream()), WebUser.class);
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(webUser.getWebUsername(),
					webUser.getWebPasswd(), new ArrayList<>()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		String token = Jwts.builder().setSubject(((User) auth.getPrincipal()).getUsername())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes()).compact();
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
	}
}
