/**
 * @author Kung den Knege
 */
public interface IRCConnectionInterface {
	void pushError(String mess);
	void pushMess(String mess);
	void connectionClosed();
	void connected();
	void established();
	void pushStatus(String mess);
	void addUser(String name);
}
