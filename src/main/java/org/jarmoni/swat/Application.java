package org.jarmoni.swat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Read:
// https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/
// http://blog.florian-hopf.de/2017/08/spring-security.html
// https://blog.jdriven.com/2014/10/stateless-spring-security-part-2-stateless-authentication/
// https://stackoverflow.com/questions/16777003/what-is-the-easiest-way-to-disable-enable-buttons-and-links-jquery-bootstrap
// https://www.freefavicon.com
@SpringBootApplication
public class Application {

	public static void main(final String[] args) {

		SpringApplication.run(Application.class, args);
	}
}
