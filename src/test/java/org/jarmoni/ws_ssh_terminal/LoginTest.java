package org.jarmoni.ws_ssh_terminal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.HEADER_STRING;
import static org.jarmoni.ws_ssh_terminal.security.SecurityConstants.TOKEN_PREFIX;

import java.util.List;

import org.jarmoni.ws_ssh_terminal.user.WebUser;
import org.jarmoni.ws_ssh_terminal.user.WebUserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LoginTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private final Gson gson = new Gson();

	@Test
	public void nullTest() throws Exception {
		// If this fails, basic setup is broken
	}

	@Test
	public void testGetTokenOk() throws Exception {

		final WebUser user = new WebUser(WebUserDetailsService.DUMMY_USER, WebUserDetailsService.DUMMY_PASSWD);
		final ResponseEntity<Void> response = this.restTemplate.exchange("/login", HttpMethod.POST,
				new HttpEntity<String>(this.gson.toJson(user)), Void.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		final List<String> authHeaders = response.getHeaders().get(HEADER_STRING);
		assertThat(authHeaders, is(notNullValue()));
		assertThat(authHeaders.size(), is(1));
		assertThat(authHeaders.get(0).startsWith(TOKEN_PREFIX), is(true));
	}

	@Test
	public void testGetTokenWrongUserFail() throws Exception {

		final WebUser user = new WebUser("johndoe", WebUserDetailsService.DUMMY_PASSWD);
		final ResponseEntity<Void> response = this.restTemplate.exchange("/login", HttpMethod.POST,
				new HttpEntity<String>(this.gson.toJson(user)), Void.class);
		assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
	}

	@Test
	public void testGetTokenWrongPasswdFail() throws Exception {

		final WebUser user = new WebUser(WebUserDetailsService.DUMMY_USER, "johndoepass");
		final ResponseEntity<Void> response = this.restTemplate.exchange("/login", HttpMethod.POST,
				new HttpEntity<String>(this.gson.toJson(user)), Void.class);
		assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
	}

}
