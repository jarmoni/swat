package org.jarmoni.ws_ssh_terminal;

import org.jarmoni.ws_ssh_terminal.ws.message.PingMessage;
import org.jarmoni.ws_ssh_terminal.ws.message.WsMessageWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;

// Read:
// https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/
// http://blog.florian-hopf.de/2017/08/spring-security.html
// https://blog.jdriven.com/2014/10/stateless-spring-security-part-2-stateless-authentication/
@SpringBootApplication
public class Application {

	public static void main(final String[] args) {
		final WsMessageWrapper wrapper = new WsMessageWrapper();
		wrapper.ping = new PingMessage("123");
		System.out.println(new Gson().toJson(wrapper));
		SpringApplication.run(Application.class, args);
	}
}
