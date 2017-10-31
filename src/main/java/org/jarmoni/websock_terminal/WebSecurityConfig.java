package org.jarmoni.websock_terminal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	// This does NOT work! ("Spring security does'nt support websockets without STOMP")
	// (https://github.com/spring-projects/spring-security/issues/3915)
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/ws").permitAll().anyRequest().authenticated().and()
				.httpBasic();
	}

	// Global access to resources (authentication happens before websocket-handshake - see: WebSocketConfig)
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/").anyRequest();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
	}
}
