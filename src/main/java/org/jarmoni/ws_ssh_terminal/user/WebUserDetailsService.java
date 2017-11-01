package org.jarmoni.ws_ssh_terminal.user;

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class WebUserDetailsService implements UserDetailsService {

	private Map<String, WebUser> users = Collections.singletonMap("user", new WebUser("user", "pass"));

	public WebUserDetailsService(final BCryptPasswordEncoder bCryptPasswordEncoder) {

		this.users = Collections.singletonMap("user", new WebUser("user", bCryptPasswordEncoder.encode("pass")));
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

		final WebUser webUser = this.users.get(username);
		if (webUser == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(webUser.getWebUsername(), webUser.getWebPasswd(), emptyList());
	}
}
