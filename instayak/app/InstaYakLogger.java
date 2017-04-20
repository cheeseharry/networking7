/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 3
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import instayak.serialization.InstaYakError;
import instayak.serialization.InstaYakMessage;

/**
 * Logger utility used to make InstaYakServer logs
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakLogger {

	/**
	 * java logger that outputs to a file called "connections.log"
	 */
	private Logger logger;
	
	/**
	 * ip address of client that this logger instance is associated with
	 */
	private String clientIp;
	
	/**
	 * port of client that this logger instance is associated with
	 */
	private String clientPort;
	
	/**
	 * id of thread that this instance is running on
	 */
	private String threadId;
	
	/**
	 * user who's client this logger instance is associated with
	 */
	private User clientUser = null;
	
	/**
	 * Constructs a new InstaYakLogger and extracts the information it needs from
	 * the socket. Once it has stored that info it logs an initialization message.
	 * 
	 * @param logger java logger utility to output to
	 * @param clntSock socket connection of client that this logger is associated with
	 */
	public InstaYakLogger(Logger logger, Socket clntSock) {
		this.logger = logger;
		this.clientPort = String.valueOf(clntSock.getPort());
		this.clientIp = clntSock.getInetAddress().getHostAddress();
		this.threadId = Thread.currentThread().getName();
		
		// initialization log message
		logInfo("Handling client " + this.clientIp + "-" + this.clientPort + 
				" with thread id " + this.threadId + System.lineSeparator());
	}
	
	/**
	 * adds an error message to the server log file
	 * 
	 * @param error error message to log
	 */
	public void logError(String error) {
		logger.log(Level.WARNING, error + "***terminated" + System.lineSeparator());
	}
	
	/**
	 * adds an info message to the server log file
	 * 
	 * @param info info message to log
	 */
	public void logInfo(String info) {
		logger.log(Level.INFO, info + System.lineSeparator());
	}
	
	/**
	 * adds an update message log to the server log file
	 * 
	 * @param msg message being sent to user
	 */
	public void updateMsg(InstaYakMessage msg) {
		String logMessage = clientIp + " " + clientPort + " ";
		
		// check if message is an InstaYakError
		if (msg.getOperation().equals(InstaYakError.operation)) {
			logError(logMessage);
		} else {
			if (clientUser == null) {
				logMessage += "Unauthenticated User";
			} else {
				logMessage += clientUser.getId();
			}
			
			logMessage += ": " + msg.toString();
			
			logInfo(logMessage);
		}
	}
	
	/**
	 * sets the user that this logger is associated with
	 * @param user updated user value
	 */
	public void setClientUser(User user) {
		this.clientUser = user;
	}
	
	/**
	 * returns the user that this logger is associated with
	 * @return current user
	 */
	public User getClientUser() {
		return clientUser;
	}
}
