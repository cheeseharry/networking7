/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 3
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class InstaYakServer {

	/**
	 * the number of milliseconds that a client socket is allowed to block
	 * before the connection is closed
	 */
	private static final int TIMEOUT = 60000;
	
	/**
	 * location of server log file
	 */
	private static final String LOG_FILE = "connections.log";
	
	/**
	 * port that server should listen to
	 */
	private static int serverPort;
	
	/**
	 * number of threads in thread pool
	 */
	private static int threadPoolSize;
	
	/**
	 * location of file that has a list of usernames and passwords
	 */
	private static String passwordFile;

	/**
	 * Starts an InstaYakServer that uses a shared thread pool to handle
	 * client connections
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		
		// make sure arguments are valid
		validateArgs(args);

		// Store arguments for use by other methods
		serverPort = Integer.parseInt(args[0]);
		threadPoolSize = Integer.parseInt(args[1]);
		passwordFile = args[2];

		try {
			// Create a server socket to accept client connection requests
			final ServerSocket servSock = new ServerSocket(serverPort);

			// Spawn a fixed number of threads to service clients
			spawnThreadPool(servSock);
			
		} catch (IOException e) {
			handleIOException(e);
		} catch (Exception e) {
			handleUnknownException(e);
		}
	}

	/**
	 * makes sure the supplied command line parameters are in a valid format
	 * and that the supplied password file actually exists.
	 * 
	 * @param args command line arguments to validate
	 * @return whether or not arguments are valid
	 */
	public static boolean validateArgs(String[] args) {

		boolean valid = true;
		
		if (args.length != 3) {
			valid = false;
		}

		try {
			// make sure <port> is an integer value
			Integer.parseInt(args[0]);
			// make sure <thread count> is an integer value
			Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			valid = false;
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
		}
		
		// If arguments aren't valid, print error message to console and terminate
		if (!valid) {
			consolePrintError("ERROR: invalid argument list");
			consolePrintError("Required arguments are <port> <thread count> <password file location>");
			terminate();
		}
		
		// If password file does not exist, print error message to console and terminate
		if (!fileExists(args[2])) {
			consolePrintError("ERROR: password file not found");
			terminate();
		}

		// arguments are valid
		return true;
	}

	/**
	 * check if a file exists at the given location
	 * 
	 * @param fileLocation location to check
	 * @return whether or not the file exists
	 */
	public static boolean fileExists(String fileLocation) {

		if (new File(fileLocation).isFile()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Print error message and stack trace to the console then terminate
	 *  
	 * @param e exception to print stack trace of
	 */
	private static void handleIOException(IOException e) {
		consolePrintError("ERROR: IOException occured during start up");
		e.printStackTrace();
		terminate();
	}
	
	/**
	 * Print error message and stack trace to the console then terminate
	 *  
	 * @param e exception to print stack trace of
	 */
	private static void handleUnknownException(Exception e) {
		consolePrintError("ERROR: Unknown exception occured during start up");
		e.printStackTrace();
		terminate();
	}
	
	/**
	 * Sets up server socket and instayak logger then prints as many threads
	 * as were specified in the thread count argument
	 * 
	 * @param servSock server socket to spawn threads from
	 */
	private static void spawnThreadPool(ServerSocket servSock) {
		
		// configure server socket
		try {
			servSock.setReuseAddress(true);
			servSock.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			consolePrintError("ERROR: unable to set ServerSocket reuse address to true");
			e.printStackTrace();
			terminate();
		}
		
		// get logger instance
		final Logger logger = Logger.getLogger("practical");
		
		// configure logger to output to "connections.log"
		FileHandler fh;
		try {
			fh = new FileHandler(LOG_FILE);
	        logger.addHandler(fh);
	        
	        // set up a simple plain text formatter for the logger to use
	        SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
		} catch (SecurityException e) {
			consolePrintError("ERROR: unable to create log file due to a security exception");
			terminate();
		} catch (IOException e) {
			consolePrintError("ERROR: unable to create log file due to an IOException");
			terminate();
		}
		
		// create as many threads as where specified in the thread count arguments and start 
		// each one
		for (int i = 0; i < threadPoolSize; i++) {
			Thread thread = spawnInstaYakThread(servSock, logger);
			thread.start();
		}
		
	}
	
	/**
	 * Sets up a new thread for handling clients and then returns it
	 * 
	 * @param servSock server socket to listen to for new connections
	 * @param logger java logger that is used to make an InstaYakLogger for the thread
	 * @return configured thread
	 */
	private static Thread spawnInstaYakThread(ServerSocket servSock, Logger logger) {
		return new Thread() {
			public void run() {
				while (true) {
					try {
						// Wait for a connection
						Socket clntSock = servSock.accept();
						
						// get list of users to pass to client handler so it can perform 
						// authentication
						ArrayList<User> users = getUserList(passwordFile);
						
						// create a new InstaYakLogger instance for this client handler to
						// use
						InstaYakLogger iyLogger = new InstaYakLogger(logger, clntSock);
						
						// Handle connection
						InstaYakProtocol.handleInstaYakClient(clntSock, iyLogger, users);
					} catch (IOException ex) {
						logger.log(Level.WARNING, "Client accept failed", ex);
					}
				}
			}
		};
	}
	
	/**
	 * Reads each row of password file and initializes a new User object which it then
	 * inserts in to an ArrayList of Users. Once all users have been added the arraylist
	 * is returned.
	 * 
	 * @param passwordFileLocation location of password file to iterate over
	 * @return list of all users in password file
	 */
	public static ArrayList<User> getUserList(String passwordFileLocation) {
		// create empty array list to insert users into
		ArrayList<User> users = new ArrayList<>();
		
		// open file
		File passwordFile = new File(passwordFileLocation);
		FileInputStream passwordFileIS = null;
		try {
			passwordFileIS = new FileInputStream(passwordFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		BufferedReader passwordFileReader = new BufferedReader(new InputStreamReader(passwordFileIS));
	 
		String line = null;
		try {
			// iterate over each line in file until the end is reached
			while ((line = passwordFileReader.readLine()) != null) {
				
				// split into id and password
				String[] creds = line.split(":");
				
				// use id and password to construct a new User
				User user = new User(creds[0], creds[1]);
				
				// insert user into list
				users.add(user);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return users;
	}

	/**
	 * prints a string to stderr with a newline
	 * 
	 * @param error string to print to stderr
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
