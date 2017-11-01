package org.jarmoni.ws_ssh_terminal.ws.message;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.Gson;

public class WsMessageWrapperTest {

	private Gson gson = new Gson();

	@Test
	public void testSerde() throws Exception {

		String json = "{\"command\":{\"line\":\"ls\"}}";
		{
			WsMessageWrapper wrapper = this.gson.fromJson(json, WsMessageWrapper.class);
			assertThat(wrapper.sshCredentials, is(nullValue()));
			assertThat(wrapper.command.getLine(), is("ls"));
		}

		{
			CommandMessage commandMessage = new CommandMessage("ls");
			WsMessageWrapper wrapper = new WsMessageWrapper();
			wrapper.command = commandMessage;
			JSONAssert.assertEquals(this.gson.toJson(wrapper), json, false);
		}
	}

}
