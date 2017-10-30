package com.devglan.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class SshHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SshHandler.class);
	
	private JSch jsch;
	
	private WebSocketSession wsSession;
	
	private InputStreamHandler isHandler;
	
	public SshHandler(WebSocketSession wsSession) throws Exception {
		
		this.jsch = new JSch();
		this.wsSession = wsSession;
		
		Session session = jsch.getSession("johndoe", "localhost", 22);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications", "password");
		session.setConfig("PubkeyAuthentication", "no");
		session.setUserInfo(new SshUserInfo());
		session.connect();
		
		Channel channel = session.openChannel("shell");
		
		PipedOutputStream pos = new PipedOutputStream();
		channel.setOutputStream(pos);
		OutputStreamHandler osHandler = new OutputStreamHandler(pos);
		Executors.newSingleThreadExecutor().submit(osHandler);
//		channel.setOutputStream(System.out);
		PipedInputStream pis = new PipedInputStream();
		channel.setInputStream(pis);
		//this.isHandler = new InputStreamHandler2(pis);
		//BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new PipedOutputStream(pis)));
		this.isHandler = new InputStreamHandler(pis);
		Executors.newSingleThreadExecutor().submit(isHandler);
		//channel.setInputStream(System.in);
		channel.connect();
		this.isHandler.write("blablubb");
		//writer.write("hallo");
		//writer.flush();
	}
	
	public void write(String s) {
		
		try {
			this.isHandler.write(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class OutputStreamHandler implements Runnable {
		
		private BufferedReader reader;
		
		public OutputStreamHandler(PipedOutputStream pos) throws Exception {
			
			PipedInputStream pis = new PipedInputStream(pos);
			this.reader = new BufferedReader(new InputStreamReader(pis));
		}

		@Override
		public void run() {
			while(true) {
				try {
					String line = reader.readLine();
					if(line != null) {
						LOG.debug("Line={}", line);
						if(line.isEmpty()) {
							line = "*";
						}
						wsSession.sendMessage(new TextMessage(line));
					}
					else {
						Thread.sleep(10L);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public class InputStreamHandler implements Runnable {
		
		private BufferedWriter writer;
		
		private BlockingQueue<String> mq = new LinkedBlockingQueue<>();
		
		public InputStreamHandler(PipedInputStream pis) throws Exception {
			
			this.writer = new BufferedWriter(new OutputStreamWriter(new PipedOutputStream(pis)));
		}
		
		
		
		public void write(String s) throws Exception {
			
			this.mq.put(s);
//			this.writer.write(s);
//			this.writer.flush();
			LOG.debug("Wrote line to queue={}", s);
		}



		@Override
		public void run() {
			
			while (true) {
				try {
					String line = this.mq.take();
					LOG.debug("Dequeued line={}", line);
					this.writer.write(line);
					this.writer.flush();
					LOG.debug("Wrote line to OS. Line={}", line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// TODO Auto-generated method stub
			
		}
	}
	
	
	public class SshUserInfo implements UserInfo, UIKeyboardInteractive {

		@Override
		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
				boolean[] echo) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPassphrase() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPassword() {
			// TODO Auto-generated method stub
			return "XXX";
		}

		@Override
		public boolean promptPassword(String message) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean promptPassphrase(String message) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean promptYesNo(String message) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void showMessage(String message) {
			
			try {
				wsSession.sendMessage(new TextMessage(message));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
