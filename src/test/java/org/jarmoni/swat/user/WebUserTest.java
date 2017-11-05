package org.jarmoni.swat.user;

import static org.hamcrest.MatcherAssert.*;

import static org.hamcrest.Matchers.*;

import org.jarmoni.swat.user.WebUser;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.Gson;

public class WebUserTest {
	
	@Test
	public void testSerde() throws Exception {
		
		Gson gson = new Gson();
		
		String json = "{'webUsername': 'johndoe', 'webPasswd': 'johndoepass'}";
		WebUser user = gson.fromJson(json, WebUser.class);
		assertThat(user.getWebUsername(), is("johndoe"));
		assertThat(user.getWebPasswd(), is("johndoepass"));
		
		JSONAssert.assertEquals(gson.toJson(user), json, false);
		
		String json2 = "{\"webUsername\":\"johndoe\",\"webPasswd\":\"johndoepass\"}";
		assertThat(gson.fromJson(json2, WebUser.class), is(user));
	}
	

}
