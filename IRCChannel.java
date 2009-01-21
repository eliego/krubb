/*
 * Created on Jan 31, 2004
 */

/**
 * @author kungdenknege
 */
class IRCChannel {
	// Connection to send data with
	private IRCConnection irc;
	
	// Channel name
	private String name;
	
	protected IRCChannel(IRCConnection irc, String name) {
		// Init membervars
		this.irc = irc;
		this.name = name;
		
		// Join channel
		irc.write("JOIN " + name);
	}
	
	public void send(String mess) {
		irc.write("PRIVMSG " + name + " :" + mess);
	}

}
