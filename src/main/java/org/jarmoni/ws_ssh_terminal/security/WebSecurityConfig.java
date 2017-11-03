package org.jarmoni.ws_ssh_terminal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class WebSecurityConfig {

	private final UserDetailsService userDetailsService;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;


	public WebSecurityConfig(final UserDetailsService userDetailsService,
			final BCryptPasswordEncoder bCryptPasswordEncoder) {

		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Configuration
	@Order(1)
	public static class LoginWebSecurity extends WebSecurityConfigurerAdapter {

		private final JwtValidator jwtValidator;

		public LoginWebSecurity(final JwtValidator jwtValidator) {

			this.jwtValidator = jwtValidator;
		}

		@Override
		protected void configure(final HttpSecurity http) throws Exception {

			//@formatter:off
			http.antMatcher("/login").cors().and().csrf().disable().addFilter(new JwtAuthenticationFilter(authenticationManager()))
			.addFilter(new JwtAuthorizationFilter(authenticationManager(), this.jwtValidator))
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().antMatchers(HttpMethod.POST, "/login").authenticated();
			//@formatter:on
		}
	}

	@Configuration
	@Order(2)
	public static class WsWebSecurity extends WebSecurityConfigurerAdapter {

		private final JwtValidator jwtValidator;

		public WsWebSecurity(final JwtValidator jwtValidator) {

			this.jwtValidator = jwtValidator;
		}

		@Override
		protected void configure(final HttpSecurity http) throws Exception {

			//@formatter:off
			http.antMatcher("/ws").cors().and().csrf().disable()
			.addFilter(new JwtWsAuthorizationFilter(authenticationManager(), this.jwtValidator))
			.authorizeRequests().antMatchers("/ws").authenticated();
			//@formatter:on
		}
	}

	@Configuration
	@Order(3)
	public static class RootPathSecurity extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(final HttpSecurity http) throws Exception {

			//@formatter:off
			http.antMatcher("/*").authorizeRequests()
			//.anyRequest().denyAll();
			.anyRequest().permitAll();
			//@formatter:on
		}
	}


	@Autowired
	public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService).passwordEncoder(this.bCryptPasswordEncoder);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}
}
