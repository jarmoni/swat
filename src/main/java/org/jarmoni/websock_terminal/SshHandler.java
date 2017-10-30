package org.jarmoni.websock_terminal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class SshHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SshHandler.class);

	private JSch jsch;

	private WebSocketSession wsSession;

	private InputStreamHandler isHandler;
	
	private OutputStreamHandler osHandler;

	public SshHandler(WebSocketSession wsSession, String host, int port, String userName, String passwd) throws Exception {

		this.jsch = new JSch();
		this.wsSession = wsSession;

		Session session = jsch.getSession(userName, host, port);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications", "password");
		session.setConfig("PubkeyAuthentication", "no");
		session.setUserInfo(new SshUserInfo(passwd));
		session.connect();

		Channel channel = session.openChannel("shell");
//		((ChannelShell)channel).setPty(false);
		((ChannelShell)channel).setPtyType("dumb");

		this.osHandler = new OutputStreamHandler(channel);
		Executors.newSingleThreadExecutor().submit(this.osHandler);
	
		this.isHandler = new InputStreamHandler(channel);
		Executors.newSingleThreadExecutor().submit(isHandler);
		channel.connect();
	}

	public void write(String s) {

		try {
			this.isHandler.write(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		
		this.isHandler.stopped = true;
		this.osHandler.stopped = true;
	}

	public class OutputStreamHandler implements Runnable {

		private Channel channel;
		private boolean stopped = false;

		public OutputStreamHandler(Channel channel) {

			this.channel = channel;
		}

		@Override
		public void run() {

			try (PipedOutputStream pos = new PipedOutputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(new PipedInputStream(pos)))) {

				this.channel.setOutputStream(pos, true);
				String line = null;
				while ((line = reader.readLine()) != null && !this.stopped) {
					try {
						LOG.debug("From SSH: Raw-line='{}'", line);
						Optional<String> parsedLine = LineParser.parseLine(line);
						if(parsedLine.isPresent()) {
							wsSession.sendMessage(new TextMessage(parsedLine.get()));
						}
					} catch (Throwable t) {
						LOG.error("Exception while sending message", t);
					}
				}
				LOG.info("OutputStreamHandler is exiting");

			} catch (Throwable t) {
				LOG.error("Exception in OutputStreamReader. Going down...", t);
			}
		}
	}

	public class InputStreamHandler implements Runnable {

		private final Channel channel;
		private boolean stopped = false;

		private BlockingQueue<String> mq;

		public InputStreamHandler(Channel channel) throws Exception {

			this.channel = channel;
			this.mq = new LinkedBlockingQueue<>();
		}

		public void write(String s) throws Exception {

			this.mq.put(s);
			LOG.debug("Wrote line to queue={}", s);
		}

		@Override
		public void run() {
			
			try(PipedInputStream pis = new PipedInputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new PipedOutputStream(pis)))) {
				
				this.channel.setInputStream(pis, true);
				
				while (!this.stopped) {
					try {
						String line = this.mq.poll();
						if(line != null) {
							LOG.debug("Dequeued line={}", line);
							writer.write(line + "\n");
							writer.flush();
							LOG.debug("Wrote line to OS. Line={}", line);
							LOG.info("channel-closed=" + channel.isClosed());
							LOG.info("channel-connected=" + channel.isConnected());
						}
						else {
							Thread.sleep(20L);
						}
						
					}
					catch(Throwable t) {
						LOG.error("Exception during polling", t);
					}
				}
			}
			catch(Throwable t) {
				LOG.error("Exception in InputStreamHandler. Going down...", t);
			}
		}
	}

	public class SshUserInfo implements UserInfo, UIKeyboardInteractive {
		
		private String passwd;
		
		public SshUserInfo(String passwd) {
			
			this.passwd = passwd;
		}

		@Override
		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
				boolean[] echo) {
			return null;
		}

		@Override
		public String getPassphrase() {
			return null;
		}

		@Override
		public String getPassword() {
			return this.passwd;
		}

		@Override
		public boolean promptPassword(String message) {
			return true;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return true;
		}

		@Override
		public boolean promptYesNo(String message) {
			return false;
		}

		@Override
		public void showMessage(String message) {}
	}
	
	public static class LineParser {
		
		public static Optional<String> parseLine(String line) {
			
			if(line.isEmpty()) {
				return Optional.empty();
			}
			else if(Byte.valueOf(line.getBytes()[0]).equals(new Byte((byte)27))) {
				return Optional.empty();
			}
			else if(line.trim().isEmpty()) {
				return Optional.empty();
			}
			else if(line.trim().equals("%")) {
				return Optional.empty();
			}
			return Optional.of(line);
		}
	}
}
