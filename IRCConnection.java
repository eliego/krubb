/**
 * @author Kung den Knege
 */
import java.net.*;
import java.io.*;

public class IRCConnection extends Thread {
// 	Input and Output
	private BufferedWriter output;
	private SafeBufferedReader input;
	private Socket soc;
	
//	Parent
	private IRCConnectionInterface parent;
	
	public IRCConnection(IRCConnectionInterface parent, String host) {
		this(parent, host, 6667);
	}
	
	public IRCConnection(IRCConnectionInterface parent, String host, int port) {
		// Initialize member vars
		this.parent = parent;
		
		try {
//			Initialize socket and connection
			soc = new Socket(host, port);
			
			OutputStreamWriter out = new OutputStreamWriter(soc.getOutputStream());
			output = new BufferedWriter(out);
			
			InputStreamReader in = new InputStreamReader(soc.getInputStream());
			input = new SafeBufferedReader(in);
			
			// Initialize thread
			setName("DataListener");
			setPriority(Thread.MAX_PRIORITY);			
			start();
		} catch (UnknownHostException uhe) {
			parent.pushError("Couldn't resolve server/gateway: " + uhe.getMessage());
		} catch (IOException ioe) {
			parent.pushError("Couldn't connect to server/gateway: " + ioe.getMessage());
		}
	}
	
	private void connected() {
		register();
		parent.connected();
	}

	public void run() {		
		java.awt.Frame frejm = new java.awt.Frame(); // Sl?ng upp ett f?nster f?r att visa att tr?den startats, debug
		frejm.show();
		connected();
		String data;
		int errors = 0;
		try {
			while ((data = input.readLine()) != null) {// Loop for data
				parse(data);
				yield();
			}
		} catch (IOException e) {
			if (++errors >= 5) {
				parent.pushError("Coultdn't read from socket: "+e.getMessage());
				close();
			}
		}
		close(); // Loopexit, connection closed		
	}
	
	public void close() {
		if (soc != null && soc.isConnected())
			try {soc.close();} catch (IOException e) {}		// Close connection
		
		parent.connectionClosed();	// Tell parent
		
		if (isAlive())
			stop();			// Stop thread
	}
	
	private void register() {
		parent.pushMess("");
		write("USER GeggePlutt localhost localhost GeggePlutt");
	}
	
	public void setNick(String nick) {
		parent.pushMess("");
		write("NICK " + nick);
	}
	
	protected void write(String mess) {
		try {
			output.write(mess + "\r\n");
			output.flush();
		} catch (IOException ioe) {
			parent.pushError("Couldn't write to socket: " + ioe.getMessage());
			close();
		}	
	}
	
	public IRCChannel joinChannel(String channel) {
		if (!channel.startsWith("#"))
			channel = "#" + channel;
		
		return new IRCChannel(this, channel);	
	}
	
	private void parse(String data) {
		int code = 0;
		String cmd = "";
		String source = "";

		if (data.startsWith(":")) {
			source = data.substring(1, data.indexOf(' '));
			data = data.substring(data.indexOf(' ') + 1);
		}
			
		cmd = data.substring(0,data.indexOf(' ')); // Parse command
		data = data.substring(data.indexOf(' ') + 1);
		
		try {
			code = Integer.parseInt(cmd);
			
			switch (code) {
				case 1:
					parent.established();
					break;
					
				case 433:
					parent.pushError("Nick already taken");
					close();
					break;
					
				case 332:
					parent.pushStatus("Topic for channel is: " + data.substring(data.indexOf(':') + 1));
					break;
					
				case 353:
					data = data.substring(data.indexOf(':') + 1);
					String user;
					while (!data.equals("")) {
						parent.addUser(data.substring(0, data.indexOf(' ')));
						data = data.substring(data.indexOf(' ') + 1);
					}
					
					break;
			}
		} catch (NumberFormatException e) {
			if (cmd.equals("PRIVMSG")) {
				source = source.substring(0,source.indexOf('!'));
				data = data.substring(data.indexOf(':') + 1);
				parent.pushMess("<" + source + "> " + data);
			} else if (cmd.equals("PING"))
				parent.pushStatus("PING");				
		}
	}

}
