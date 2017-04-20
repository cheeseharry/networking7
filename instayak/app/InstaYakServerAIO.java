/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 7
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * InstaYakServer server using asynchronous I/O
 * @version 1.0
 */
public class InstaYakServerAIO {
	
	/**
	 * location of server log file
	 */
	private static final String LOG_FILE = "connections.log";
	
	/**
	 * port that server should listen to
	 */
	private static int serverPort;

	/**
	 * location of file that has a list of usernames and passwords
	 */
	private static String passwordFile;

	/**
	 * Logger for server
	 */
	protected static final Logger logger = Logger.getLogger("InstaYakServerAIO");

	// Configure logger handler (connections.log) and format (simple)
	static {
		logger.setUseParentHandlers(false);
		try {
			Handler handler = new FileHandler(LOG_FILE);
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
		} catch (Exception e) {
			System.err.println("Unable to initialized logger");
			System.exit(1);
		}
	}

	/**
	 * Runs InstaYakServerAIO
	 * @param args command line arguments
	 */
	public static void main(final String[] args) {
		// Test for args correctness and process
		validateArgs(args);
		
		// Store arguments for use by other methods
		serverPort = Integer.parseInt(args[0]);
		passwordFile = args[1];

		// Create listening socket channel
		AsynchronousServerSocketChannel listenChannel = null;
		try {
			// Bind local port
			listenChannel = AsynchronousServerSocketChannel.open().bind(
					new InetSocketAddress(serverPort));
			// Create accept handler
			listenChannel.accept(null,
					makeAcceptCompletionHandler(listenChannel, logger));
		} catch (IOException ex) {
			System.err.println("Unable to create server socket channel: "
					+ ex.getMessage());
			System.exit(1);
		}
		// Block until current thread dies
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Create completion handler for accept
	 * 
	 * @param listenChannel channel listening for new clients
	 * @param logger server logger
	 * 
	 * @return completion handler
	 */
	public static CompletionHandler<AsynchronousSocketChannel, Void> makeAcceptCompletionHandler(
			final AsynchronousServerSocketChannel listenChannel, final Logger logger) {
		return new CompletionHandler<AsynchronousSocketChannel, Void>() {
			/* 
			 * Called when accept completes
			 * 
			 * @param clntChan channel for new client
			 * @param v void means no attachment
			 */
			@Override
			public void completed(AsynchronousSocketChannel clntChan, Void v) {
				logger.info("Handling accept for " + clntChan);
				System.out.println("Handling accept for " + clntChan);
				listenChannel.accept(null, this);
				TCPAIODispatcher aioDispatcher = new TCPAIODispatcher(new InstaYakAIOHandler(passwordFile), logger);
				aioDispatcher.handleAccept(clntChan);
			}

			/*
			 * Called if accept fails
			 * 
			 * @param ex exception triggered by accept failure
			 * @param v void means no attachment
			 */
			@Override
			public void failed(Throwable ex, Void v) {
				logger.log(Level.WARNING, "accept failed", ex);
			}
		};
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
		
		if (args.length != 2) {
			valid = false;
		}

		try {
			// make sure <port> is an integer value
			Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			valid = false;
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
		}
		
		// If arguments aren't valid, print error message to console and terminate
		if (!valid) {
			consolePrintError("ERROR: invalid argument list");
			consolePrintError("Required arguments are <port> <thread count> <password file location>");
			throw new IllegalArgumentException("Parameter(s): <Port> <Password File>");
		}
		
		// If password file does not exist, print error message to console and terminate
		if (!fileExists(args[1])) {
			consolePrintError("ERROR: password file not found");
			throw new IllegalArgumentException("Parameter(s): <Port> <Password File>");
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
	 * prints a string to stderr with a newline
	 * 
	 * @param error string to print to stderr
	 */
	private static void consolePrintError(String error) {
		System.err.println(error);
	}

}

