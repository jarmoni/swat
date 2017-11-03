package org.jarmoni.ws_ssh_terminal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.HEADER_STRING;
import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.TOKEN_PREFIX;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.core.HandshakeException;
import org.jarmoni.ws_ssh_terminal.user.WebUser;
import org.jarmoni.ws_ssh_terminal.user.WebUserDetailsService;
import org.jarmoni.ws_ssh_terminal.ws.message.PingMessage;
import org.jarmoni.ws_ssh_terminal.ws.message.WsMessageWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class WsTest {

	private static final Logger LOG = LoggerFactory.getLogger(WsTest.class);

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private final Gson gson = new Gson();

	private final String uuid = UUID.randomUUID().toString();

	private WsMessageWrapper pingWrapper;

	private final String staleToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTUxMDQwMjAwMn0.M4wpPmKaygN9FcVl9wUVBwH2xMqFgBSr6eZy-CwJ_QYTLI6Padf0_HGcEcmbKgUS338pM65BM7sC2zaWDuO0Jg";

	@Before
	public void setUp() throws Exception {

		this.pingWrapper = new WsMessageWrapper();
		this.pingWrapper.ping = new PingMessage(this.uuid);

	}

	@Test
	public void testConnFailsWrongTokenGiven() throws Exception {

		try {
			this.createWsConnection(String.format("ws://localhost:%s/ws?Authorization=%s", this.port, this.staleToken));
			fail("this test should fail!");

		}
		catch(final Throwable t) {
			assertThat(t.getCause() instanceof HandshakeException, is(true));
		}
	}

	@Test
	public void testConnWithGoodTokenOk() throws Exception {

		final WebUser user = new WebUser(WebUserDetailsService.DUMMY_USER, WebUserDetailsService.DUMMY_PASSWD);
		final ResponseEntity<Void> response = this.restTemplate.exchange("/login", HttpMethod.POST,
				new HttpEntity<String>(this.gson.toJson(user)), Void.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		final List<String> authHeaders = response.getHeaders().get(HEADER_STRING);
		final String token = authHeaders.get(0).replace(TOKEN_PREFIX, "");

		this.createWsConnection(String.format("ws://localhost:%s/ws?Authorization=%s", this.port, token));
	}


	private void createWsConnection(final String url) throws Exception {

		final CountDownLatch cdl = new CountDownLatch(1);

		final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

		final ClientManager client = ClientManager.createClient();
		client.connectToServer(new Endpoint() {

			@Override
			public void onOpen(final Session session, final EndpointConfig config) {
				try {
					session.addMessageHandler((Whole<String>) message -> {
						System.out.println(">>> Received message: " + message);
						cdl.countDown();
					});
					session.getBasicRemote().sendText(gson.toJson(pingWrapper));
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}, cec, new URI(String.format(url, this.port)));

		final boolean success = cdl.await(5, TimeUnit.SECONDS);
		if(!success) {
			fail("No result received");
		}
	}
}
