package org.jarmoni.ws_ssh_terminal.user;

public class WebUser {

	public static final String PROP_USERNAME = "webUsername";
	public static final String PROP_PASSWD = "webPasswd";
	
	private String webUsername;
	private String webPasswd;

	public WebUser(String webUsername, String webPasswd) {
		
		this.webUsername = webUsername;
		this.webPasswd = webPasswd;
	}

	public String getWebUsername() {
		return webUsername;
	}

	public String getWebPasswd() {
		return webPasswd;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((webPasswd == null) ? 0 : webPasswd.hashCode());
		result = prime * result + ((webUsername == null) ? 0 : webUsername.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebUser other = (WebUser) obj;
		if (webPasswd == null) {
			if (other.webPasswd != null)
				return false;
		} else if (!webPasswd.equals(other.webPasswd))
			return false;
		if (webUsername == null) {
			if (other.webUsername != null)
				return false;
		} else if (!webUsername.equals(other.webUsername))
			return false;
		return true;
	}
}
