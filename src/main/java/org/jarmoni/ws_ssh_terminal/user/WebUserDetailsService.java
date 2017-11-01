package org.jarmoni.ws_ssh_terminal.user;

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class WebUserDetailsService implements UserDetailsService {
	
	Map<String, WebUser> users = Collections.singletonMap("user", new WebUser("user", "pass"));

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		WebUser webUser = this.users.get(username);
		if (webUser == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(webUser.getWebUsername(), webUser.getWebPasswd(), emptyList());
	}
}
