const TOKEN_PREFIX = "Bearer ";
const HEADER_STRING = "Authorization";
const LOGIN_URL_POSTFIX = '/login';
const WS_URL_POSTFIX = '/ws?' + HEADER_STRING + '=';
const WELCOME_MSG = "Welcome to S.W.A.T.!\n" +
		"To start a new SSH-session you have to...\n" +
		"- Enter adress and credentials ('Settings').\n" +
		"- Press 'Connect' for launching the Session.\n" +
		"- Press 'Disonnect' to terminate the session and free all resources.\n";