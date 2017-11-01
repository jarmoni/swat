package org.jarmoni.ws_ssh_terminal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Read:
// https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/
// http://blog.florian-hopf.de/2017/08/spring-security.html
// https://blog.jdriven.com/2014/10/stateless-spring-security-part-2-stateless-authentication/
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
