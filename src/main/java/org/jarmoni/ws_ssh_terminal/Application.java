package org.jarmoni.ws_ssh_terminal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Read:
// https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/
// http://blog.florian-hopf.de/2017/08/spring-security.html
@SpringBootApplication
public class Application {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
