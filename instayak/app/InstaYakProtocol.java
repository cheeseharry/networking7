/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 3
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import instayak.serialization.*;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Protocol for handling an InstaYakClient
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakProtocol implements Runnable {
	
	/**
	 * list of users that were in credentials file
	 */
	private ArrayList<User> users;
		
	/**
	 * Socket connection to client
	 */
	private Socket clntSock; 
	
	/**
	 * Server logger
	 */
	private InstaYakLogger logger; 

	/**
	 * Constructs InstaYakProtocol instance
	 * 
	 * @param clntSock socket connection to client
	 * @param logger server logger
	 * @param users list of users that were in credentials file
	 */
	public InstaYakProtocol(Socket clntSock, Logger logger, ArrayList<User> users) {
		this.clntSock = clntSock;
		this.logger = new InstaYakLogger(logger, clntSock);
		this.users = users;
	}

	/**
	 * handles a session with a single InstaYakClient and then when the
	 * session is over it closes the connection and returns
	 * 
	 * @param clntSock socket connection to client
	 * @param logger server logger
	 * @param users list of users that were in credentials file
	 */
	public static void handleInstaYakClient(Socket clntSock, InstaYakLogger logger, ArrayList<User> users) {
		try {		
			// get message input/outputs
			MessageInput in = getMessageInput(clntSock);
			MessageOutput out = getMessageOutput(clntSock);
			
			if (in == null || out == null) { 
				return;
			}

			// initiate authentication protocol with new client
			User user = authenticate(out, in, users, logger);
			
			if(user == null) { // authentication failed
				return; 
			}
			
			Twitter twitter = new TwitterFactory().getInstance();
			
			while (true) {
				// wait for client to send next message
				InstaYakMessage message = getNextMessage(in, logger);

				// hand message off to message handler
				handleMessage(message, user, twitter, out, logger);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				clntSock.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Gets called by JVM when new thread is spawned. Initiates handling
	 * of InstaYakClient.
	 */
	public void run() {
		handleInstaYakClient(clntSock, logger, users);
	}
	
	/**
	 * Initializes a message input from a socket and passes it back to the
	 * caller. If there is an error it prints the error and terminates.
	 * 
	 * @param socket uses input stream from socket to initialize MessageInput
	 * @return initialized MessageInput
	 */
	private static MessageInput getMessageInput(Socket socket) {
		
		try {
			return new MessageInput(socket.getInputStream());
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Initializes a message output from a socket and passes it back to the
	 * caller. If there is an error it prints the error and terminates.
	 * 
	 * @param socket uses output stream from socket to initialize MessageOutput
	 * @return initialized MessageOutput
	 */
	private static MessageOutput getMessageOutput(Socket socket) {
		
		try {
			return new MessageOutput(socket.getOutputStream());
		} catch (NullPointerException e) {
			//consolePrintError(NULL_POINTER_ERROR);
			//terminate();
		} catch (IOException e) {
			//consolePrintError(IO_ERROR);
			//terminate();
		}
		
		return null;
	}
	
	/**
	 * Carries out authentication protocol with client
	 * 
	 * @param out MessageOutput to send message to client with
	 * @param in MessageInput to receive message from client
	 * @param users list of instayak users
	 * @param logger InstaYakLogger for keeping track of messages
	 * @return user if successfully authenticated, null otherwise
	 * @throws InstaYakException if there is a message parsing error
	 * @throws IOException if there is an IO error
	 */
	private static User authenticate(MessageOutput out, MessageInput in, ArrayList<User> users, InstaYakLogger logger)
			throws InstaYakException, IOException {
		
		// send InstaYakVersion message to client
		sendVersionNumber(out, logger);
		
		// wait for client to respond with an InstaYakID message
		InstaYakID iyid = waitForIdResponse(in, logger);
		
		// see if there are any users with the given ID
		User user = findUser(iyid.getID(), users);
		
		// if no users were found then send/log an error and terminate
		if (user == null) {
			InstaYakError error = new InstaYakError("No such user " + iyid.getID());
			sendNextMessage(out, error, logger);
			return null;
		} else {
			logger.setClientUser(user);
		}
		
		// generate a random nonce to use for an InstaYakChallenge message
		String nonce = generateNonce();
		
		// send nonce to client in an InstaYakChallenge message
		sendChallenge(out, nonce, logger);
		
		// wait for client to respond with an InstaYakCredentials message
		String hash = waitForCredentials(in, logger);
		
		// check if InstaYakCredentials message had a valid hash or not
		if (!validateHash(hash, user.getPassword(), nonce)) {
			InstaYakError error = new InstaYakError("Unable to authenticate");
			sendNextMessage(out, error, logger);
			return null;
		} else {
			sendAck(out, logger);
			return user;
		}
	}
	
	/**
	 * waits until server has send a message to the client. Once it has a
	 * message it decodes it. If the message is an Error it prints the error
	 * and terminates. Otherwise it passes the InstaYakMessage back to the
	 * caller.
	 * 
	 * @return InstaYakMessage that was received from the server
	 * @throws InstaYakException if message is unable to be decoded
	 */
	private static InstaYakMessage getNextMessage(MessageInput in, InstaYakLogger logger) 
			throws InstaYakException {

		try {
			InstaYakMessage message = InstaYakMessage.decode(in);
			
			if (message.getOperation().equals(InstaYakError.operation)) {
				System.err.println(message);
			}
			
			logger.updateMsg(message);
			
			return message;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * sends a message to the client and logs the message contents
	 * 
	 * @param out MessageOutput to send message to client with
	 * @param msg message to send to client
	 * @param logger InstaYakLogger for logging message 
	 * @throws IOException if there is an issue encoding the message
	 */
	private static void sendNextMessage(MessageOutput out, InstaYakMessage msg, InstaYakLogger logger) 
			throws IOException {
		
		logger.updateMsg(msg);
		msg.encode(out);
	}
	
	/**
	 * Sends first message to client which is an InstaYakVersion message.
	 * Client should respond with an InstaYakID message.
	 * @throws IOException if encode encounters an IO issue
	 */
	private static void sendVersionNumber(MessageOutput out, InstaYakLogger logger) throws IOException {
		InstaYakVersion iyv = new InstaYakVersion();
		sendNextMessage(out, iyv, logger);
	}
	
	/**
	 * Waits for client to respond with an InstaYakID message and then
	 * returns the ID that was sent by the client.
	 * 
	 * @throws InstaYakException if clients response is not an InstaYakID message
	 * @return ID that client sent
	 */
	private static InstaYakID waitForIdResponse(MessageInput in, InstaYakLogger logger) 
			throws InstaYakException {
		
		InstaYakMessage iym = getNextMessage(in, logger);
		if (iym.getOperation() != InstaYakID.operation) {
			throw new InstaYakException("Client did not respond with InstaYakID message");
		}
		
		return (InstaYakID)iym;
	}
	
	/**
	 * Creates a new InstaYakChallenge message using the nonce that is
	 * passed in and then send the message to the client. Client should
	 * respond with an InstaYakCredentials message.
	 * 
	 * @param nonce number used once that is used to create a new InstaYakChallenge message
	 */
	private static void sendChallenge(MessageOutput out, String nonce, InstaYakLogger logger) {
		InstaYakChallenge iyc = null;
		
		try {
			iyc = new InstaYakChallenge(nonce);
		} catch (InstaYakException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			sendNextMessage(out, iyc, logger);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Waits for clients response from the InstaYakChallenge it was sent. Once
	 * it receives an InstaYakCredentials message, it passes back the hash that
	 * it contains.
	 * 
	 * @throws InstaYakException if clients response is not an InstaYakCredentials message
	 * @return hash from client's InstaYakCredentials message
	 */
	private static String waitForCredentials(MessageInput in, InstaYakLogger logger) 
			throws InstaYakException {
		
		InstaYakMessage iym = getNextMessage(in, logger);
		if (iym.getOperation() != InstaYakCredentials.operation) {
			throw new InstaYakException("Client did not respond with InstaYakID message");
		}
		
		return ((InstaYakCredentials)iym).getHash();
	}
	
	/**
	 * sends an InstaYakACK message to the client
	 * 
	 * @param out MessageOutput to send message to the client with
	 * @param logger InstaYakLogger used to record that the ACK was sent
	 */
	private static void sendAck(MessageOutput out, InstaYakLogger logger) {
		InstaYakACK ack = new InstaYakACK();
		
		try {
			sendNextMessage(out, ack, logger);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Searches for a user in the user list by id
	 * 
	 * @param id value to search for
	 * @param users list of users to search through
	 * @return user with given id or null if not found
	 */
	public static User findUser(String id, ArrayList<User> users) {
		
		for (User user : users) {
			if (user.getId().equals(id)) {
				return user;
			}
		}
		
		return null;
	}
	
	/**
	 * generates a random nonce to use when creating an InstaYakChallenge message
	 * 
	 * @return random nonce
	 */
	private static String generateNonce() {
		return String.valueOf(ThreadLocalRandom.current().nextInt(1, 1000000));
	}
	
	/**
	 * Compares the hash that the client sends back to the correct hash value and returns
	 * a boolean for whether or not the clients hash was valid
	 * 
	 * @param hash clients response in InstaYakCredentials message
	 * @param password users password that is used to compute
	 * @param nonce nonce that is used to compute the hash
	 * @return whether or not clients hash is valid
	 */
	private static boolean validateHash(String hash, String password, String nonce) {
		
		String computedHash = "";
		
		try {
			computedHash = ComputeHash.computeHash(nonce + password);
		} catch (UnsupportedEncodingException e) {
			System.out.println("unsupported encoding exception");
		}
		
		return hash.equals(computedHash);
	}
	
	/**
	 * determines if client sent an InstaYakUOn or an InstaYakSLMD and then passes
	 * the message on to the correct handler
	 * 
	 * @throws InstaYakException if message is neither an InstaYakUOn or an InstaYakSLMD
	 * @param message InstaYakMessage that client sent after it was authenticated
	 */
	private static void handleMessage(InstaYakMessage message, User user, Twitter twitter, MessageOutput out, InstaYakLogger logger)
			throws InstaYakException {
		
		try {
			if (message.getOperation().equals(InstaYakUOn.operation)) {
				postUOnMessage(twitter, (InstaYakUOn)message, user);
			} else if (message.getOperation().equals(InstaYakSLMD.operation)) {
				postSLMDMessage(twitter, user);
			} else {
				throw new InstaYakException("Client did not send UOn or SLMD message");
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		sendAck(out, logger);
	}
	
	/**
	 * posts an InstaYakUOn message to twitter
	 * 
	 * @param uon InstaYakUOn message to post to twitter
	 * @param user sender of uon message
	 * @throws TwitterException 
	 */
	private static void postUOnMessage(Twitter twitter, InstaYakUOn uon, User user) throws TwitterException {
		updateStatus(twitter, user.getId() + ": UOn #" + uon.getCategory(), uon.getImage());
	}
	
	/**
	 * posts an InstaYakSLMD message to twitter
	 * 
	 * @param user sender of slmd message
	 * @throws TwitterException 
	 */
	private static void postSLMDMessage(Twitter twitter, User user) throws TwitterException {
		updateStatus(twitter, user.getId() + ": SLMD " + user.getSlmdCount(), null);
		user.incrementSlmdCount();
	}
	
	/**
	 * Adds a new post to the twitter wall
	 * 
	 * @param twitter instance of Twitter4J
	 * @param message text contents of new post
	 * @param image image to put in post
	 * @return the status object
	 * @throws TwitterException if there is an issue updating the status
	 */
	private static Status updateStatus(final Twitter twitter, final String message, final byte[] image)
			throws TwitterException {
		
		// create new update instance with message contents
		StatusUpdate update = new StatusUpdate(message);
		
		// check if an image was passed in
		if (image != null) {
			// convert byte array to input stream so it will be compatible with Twitter4J API
			InputStream imageStream = new ByteArrayInputStream(image);
			update.media("image", imageStream);
		}
		
		// send status to twitter
		return twitter.updateStatus(update);
	}
	
}




















