package org.jarmoni.ws_ssh_terminal.security;

import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.SECRET;
import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.TOKEN_PREFIX;

import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import io.jsonwebtoken.Jwts;

public class JwtValidator {
	
	public UsernamePasswordAuthenticationToken validateToken(String token) {
		
		if (token != null) {
			// parse the token.
			String user = Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody().getSubject();

			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}
			return null;
		}
		return null;
	}

}
