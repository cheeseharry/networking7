/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 2
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app.test;

import instayak.serialization.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * InstaYak console application that allows users to login and send UOn/SLMD messages
 * 
 * @version 1.0
 * @author Kevin Aud
 */
public class HardCodedInstaYakClient {
	
	// User Arguments Errors
	private final static String INVALID_ARGS_ERROR = "Invalid Arguments\nCorrect Format is: <server> <port> <userid> <password>";

	// Invalid Message Errors
	private final static String VERSION_ERROR = "Invalid message: bad version number";
	
	// Communication Errors
	private final static String UNKNOWN_HOST_ERROR = "Unable to communicate: unknown host";
	private final static String IO_ERROR = "Unable to communicate: io exception";
	private final static String NULL_POINTER_ERROR = "Unable to communicate: null pointer exception";
	private final static String UNKOWN_ENCODING_ERROR = "Unable to communicate: unknown encoding exception";
	
	// Validation Errors
	private final static String INVALID_CHALLENGE_ERROR = "Validation failed: bad challenge";
	private final static String INVALID_USER_ID_ERROR = "Validation failed: bad user id";
	private final static String CREDENTIALS_ERROR = "Validation failed: unable to send credentials";
	private final static String INVALID_HASH_ERROR = "Validation failed: hash could not be generated";
	
	// Unexpected Message Errors
	private final static String UNEXPECTED_MESSAGE_ERROR = "Unexpected message: ";
	
	// User supplied arguments
	private static String host;
	private static int port;
	private static String userid;
	private static String password;
	
	// Input/Output buffers for communication with server
	private static MessageInput in;
	private static MessageOutput out;
	
	public static void main(String[] args) throws IOException, InstaYakException {
		
		// Make sure valid arguments were given and terminate if not
		if (!validArgs(args)) {
			consolePrintError(INVALID_ARGS_ERROR);
			terminate();
		}
		
		// Store arguments for use by other methods
		host = args[0];
		port = Integer.parseInt(args[1]);
		userid = args[2];
		password = args[3];	
		
		// Connect to server
		Socket socket = connect(host, port);
		
		// Create input/output buffers
		in = getMessageInput(socket);
		out = getMessageOutput(socket);
		
		verifyVersion();
		
		authenticate();
		
		while (true) {
			
		}
		/*InstaYakACK ack = new InstaYakACK();
		
		System.out.println("sending ack...");
		ack.encode(out);
		
		InstaYakMessage response = getNextMessage();
		System.out.println(response.toString());*/
		
	}

	/**
	 * Checks whether or not valid arguments were passed in from the command
	 * line
	 * 
	 * @param args command line arguments
	 * @return whether or not command line arguments are valid
	 */
	public static boolean validArgs(String[] args) {
		
		if (args.length != 4) {
			return false;
		}
		
		try {
	        Integer.parseInt(args[1]);
	    }
	    catch(NumberFormatException e) {
	        return false;
	    }
		
		return true;
	}
	
	/**
	 * Attempts to open a new socket connection to the host and port that the
	 * user specified and returns the socket if it successfully connects
	 * 
	 * @param host server to connect to
	 * @param port port on server to connect to
	 * @return socket connection
	 */
	private static Socket connect(String host, int port) {
		
		try {
			
			return new Socket(host, port);
			
		} catch (UnknownHostException e) {
			
			consolePrintError(UNKNOWN_HOST_ERROR);
			terminate();
			
		} catch (IOException e) {
			
			consolePrintError(IO_ERROR);
			terminate();
			
		}
		
		return null;	
	}
	
	/**
	 * waits for server to send version message and ensures it is valid. If it
	 * is not valid then it prints a version error message and terminates.
	 */
	private static void verifyVersion() {
		
		try {
			InstaYakMessage message = getNextMessage();
			
			if (!message.getOperation().equals("INSTAYAK")){
				consolePrintError(UNEXPECTED_MESSAGE_ERROR + message.getOperation());
				verifyVersion();
			} else if (!((InstaYakVersion)message).getVersion().equals("1.0")) {
				consolePrintError(VERSION_ERROR);
				consolePrint(((InstaYakVersion)message).getVersion());
				terminate();
			}
			
		} catch (InstaYakException e) {
			consolePrintError(VERSION_ERROR);
			System.out.print(e.getStackTrace());
			terminate();
		}
		
	}
	
	/**
	 * attempts to authenticate the user with the credentials that were
	 * provided as arguments from the command line. If it is unable to
	 * authenticate with the server, it prints and error messsage and
	 * terminates.
	 */
	private static void authenticate() {
		
		InstaYakID iyID = null;
		
		try {
			 iyID = new InstaYakID(userid);
		} catch (InstaYakException e) {
			consolePrintError(INVALID_USER_ID_ERROR);
			terminate();
		}
		
		InstaYakChallenge iyChallenge = requestChallenge(iyID);
		InstaYakCredentials iyCreds = getCredentials(iyChallenge.getNonce());
		
		try {
			iyCreds.encode(out);
		} catch (IOException e) {
			consolePrintError(IO_ERROR);
			terminate();
		}
		
		try {
			InstaYakMessage message = getNextMessage();
			
			if (message.getOperation() != InstaYakACK.operation) {
				consolePrintError(UNEXPECTED_MESSAGE_ERROR + message.toString());
				terminate();
			}
		} catch (InstaYakException e) {
			consolePrintError(CREDENTIALS_ERROR);
			terminate();
		}
		
	}

	/**
	 * sends an InstaYakID message to the server and waits for the server to 
	 * respond with a challenge. If the server does not respond with a valid
	 * challenge, an error message is printed and the program terminates.
	 * 
	 * @param iyID InstaYakID message to send to the server
	 * @return InstaYakChallenge message that the server responded with
	 */
	private static InstaYakChallenge requestChallenge(InstaYakID iyID) {
		try {
			iyID.encode(out);
		} catch (IOException e) {
			consolePrintError(IO_ERROR);
			terminate();
		}
		
		try {
			InstaYakMessage message = getNextMessage();
			if (message.getOperation().equals(InstaYakChallenge.operation)) {
				return (InstaYakChallenge)message;
			}
		} catch (InstaYakException e) {
			consolePrintError(INVALID_CHALLENGE_ERROR);
			terminate();
		}
		
		return null;
	}
	
	/**
	 * Hashes the nonce + password and stores the result in an InstaYakCredentials
	 * message. If there is any issues it prints and error message an terminates.
	 * 
	 * @param nonce nonce that the server sent in an InstaYakChallenge message
	 * @return InstaYakCredentials message that has the hash stored in it
	 */
	private static InstaYakCredentials getCredentials(String nonce) {
		
		String hash = "";
		
		try {
			hash = ComputeHash.computeHash(nonce + password);
		} catch (UnsupportedEncodingException e) {
			consolePrintError(UNKOWN_ENCODING_ERROR);
			terminate();
		}
		
		InstaYakCredentials iyCreds = null;
		
		try {
			iyCreds = new InstaYakCredentials(hash);
		} catch (InstaYakException e) {
			consolePrintError(INVALID_HASH_ERROR);
			terminate();
		}
		
		return iyCreds;
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
	private static InstaYakMessage getNextMessage() throws InstaYakException {

		//boolean messageRead = false;
	//	while (!messageRead) {
			//if (in.hasNext()) {
				try {
					InstaYakMessage message = InstaYakMessage.decode(in);
		//			messageRead = true;
					
					if (message.getOperation().equals(InstaYakError.operation)) {
						System.err.println(message);
						terminate();
					}
					
					consolePrintln(message.toString());
					
					return message;
				} catch (IOException e) {
					consolePrintError(IO_ERROR);
					terminate();
				}
		//	}
		//}
		return null;
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
			consolePrintError(NULL_POINTER_ERROR);
			terminate();
		} catch (IOException e) {
			consolePrintError(IO_ERROR);
			terminate();
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
			consolePrintError(NULL_POINTER_ERROR);
			terminate();
		} catch (IOException e) {
			consolePrintError(IO_ERROR);
			terminate();
		}
		
		return null;
	}
	
	/**
	 * prints a string to stdout with out a newline
	 * 
	 * @param output string to print to the console
	 */
	private static void consolePrint(String output) {
		System.out.print(output);
	}
	
	/**
	 * prints a string to stdout with a newline
	 * 
	 * @param output string to print to the console
	 */
	private static void consolePrintln(String output) {
		System.out.println(output);
	}
	
	/**
	 * prints a string to stderr with a newline
	 * 
	 * @param error string to stderr
	 */
	private static void consolePrintError(String error) {
		System.err.println(error);
	}
	
	/**
	 * exits the program
	 */
	private static void terminate() {
		System.exit(0);
	}

}
