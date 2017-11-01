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


// This is just a dummy. Should be replaced with a 'real' implementation.
// Should be moved to 'test'-package (is used in unit-tests as well)
@Service
public class WebUserDetailsService implements UserDetailsService {

	public static final String DUMMY_USER = "user";
	public static final String DUMMY_PASSWD = "passwd";

	private final Map<String, WebUser> users;

	public WebUserDetailsService(final BCryptPasswordEncoder bCryptPasswordEncoder) {

		this.users = Collections.singletonMap("user", new WebUser(DUMMY_USER, bCryptPasswordEncoder.encode(DUMMY_PASSWD)));
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
