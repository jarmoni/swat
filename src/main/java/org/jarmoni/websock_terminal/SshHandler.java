package org.jarmoni.websock_terminal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

public class SshHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SshHandler.class);

	private JSch jsch;

	private WebSocketSession wsSession;

	private InputStreamHandler isHandler;

	private OutputStreamHandler osHandler;

	public SshHandler(WebSocketSession wsSession, String sshHost, int sshPort, String sshUser, String sshPasswd)
			throws Exception {

		this.jsch = new JSch();
		this.wsSession = wsSession;

		LOG.info("Connecting to SSH with credentials: sshHost={}, sshPort={}, sshUser={}, sshPasswd={}", sshHost,
				sshPort, sshUser, sshPasswd);

		Session session = jsch.getSession(sshUser, sshHost, sshPort);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications", "password");
		session.setConfig("PubkeyAuthentication", "no");
		// We don't need the SSH-User-Info-stuff for our use-case
		session.setPassword(sshPasswd);
		session.connect();

		Channel channel = session.openChannel("shell");
		// ((ChannelShell)channel).setPty(false);
		((ChannelShell) channel).setPtyType("dumb");

		this.osHandler = new OutputStreamHandler(channel);
		Executors.newSingleThreadExecutor().submit(this.osHandler);

		this.isHandler = new InputStreamHandler(channel);
		Executors.newSingleThreadExecutor().submit(isHandler);
		channel.connect();
	}

	public void stop() {

		this.isHandler.stop();
		this.osHandler.stop();
	}

	// Sends terminal-output of SSH via Websocket to UI
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
						if (parsedLine.isPresent()) {
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

		// Quite a mess to kill BufferedReader's blocking #readLine()...
		// This stop-method is not sufficient, cause stream will not be closed correctely. FIXME!
		public void stop() {

			this.stopped = true;
		}
	}

	// Receives user-input via websocket and writes to SSH's InputStream
	public void write(String s) {

		this.isHandler.sendToSsh(s);
	}

	public class InputStreamHandler implements Runnable {

		private final Channel channel;
		private boolean stopped = false;

		private BlockingQueue<String> mq;

		public InputStreamHandler(Channel channel) {

			this.channel = channel;
			this.mq = new LinkedBlockingQueue<>();
		}

		public void sendToSsh(String s) {

			try {
				this.mq.put(s);
				LOG.debug("Enqueued line={}", s);
			} catch (InterruptedException e) {
				LOG.error("Thread interrupted while enqeueing", e);
			}
		}

		@Override
		public void run() {

			try (PipedInputStream pis = new PipedInputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new PipedOutputStream(pis)))) {

				this.channel.setInputStream(pis, true);

				while (!this.stopped) {
					try {
						String line = this.mq.poll();
						if (line != null) {
							writer.write(line + "\n");
							writer.flush();
							LOG.debug("Wrote line to OS. Line={}", line);
						} else {
							Thread.sleep(20L);
						}

					} catch (Throwable t) {
						LOG.error("Exception during polling", t);
					}
				}
				LOG.info("InputStreamHandler is exiting");
			} catch (Throwable t) {
				LOG.error("Exception in InputStreamHandler. Going down...", t);
			}
		}

		// This is sufficient thus non-blocking
		public void stop() {

			this.stopped = true;
		}
	}

	// We have no 'real' terminal, so output of SSH contains some 'ugly' characters,
	// which will be filtered here.
	// Is a bit of a hack at the moment...
	public static class LineParser {

		public static Optional<String> parseLine(String line) {

			if (line.isEmpty()) {
				return Optional.empty();
			} else if (Byte.valueOf(line.getBytes()[0]).equals(new Byte((byte) 27))) {
				return Optional.empty();
			} else if (line.trim().isEmpty()) {
				return Optional.empty();
			} else if (line.trim().equals("%")) {
				return Optional.empty();
			}
			return Optional.of(line);
		}
	}
}
