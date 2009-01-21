import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Kung den Knege
 *
 */
public class KrubbMain extends Applet implements IRCConnectionInterface, ActionListener {
	// Holder for GUI elements
	private TextArea chat;
	private List users;
	private TextField mess;
	private Button send;
	
	// Application parts
	IRCConnection irc;
	IRCChannel chan;
	
	final private String nick = "Gregers_Grunka";
	final private String channel = "#wannabe";
	
	public void init() {
		initComponents(); // Display GUI
		irc = new IRCConnection(this, "lairdham.no-ip.com",6667);
	}
	
	public void connected() {
		irc.setNick(nick);
	}
	
	private void initComponents() {
		GridBagConstraints gridBagConstraints;

		chat = new TextArea();
		users = new List();
		mess = new TextField();
		send = new Button();
		
		setLayout(new java.awt.GridBagLayout());

		chat.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		add(chat, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.5;
		gridBagConstraints.weighty = 1.0;
		add(users, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		add(mess, gridBagConstraints);

		send.setLabel("Skicka!");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		add(send, gridBagConstraints);
		
		send.addActionListener(this);
	}

	public void pushError(String mess) {
		chat.append(mess + "\n");
	}

	public void pushMess(String mess) {
		chat.append(mess+"\n");
	}

	public void connectionClosed() {
		pushStatus("Disconnected from server");
	}
	
	public void stop()
	{
		irc.close();
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(send))
			if (mess.getText() != "") {
				chan.send(mess.getText());
				mess.setText("");
			}
	}

	public void established() {
		chan = irc.joinChannel(channel);		
	}

	public void pushStatus(String mess) {
		chat.append(mess + "\n");
	}

	public void addUser(String name) {
		users.add(name);
	}

}
