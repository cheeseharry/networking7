/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 7
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import instayak.serialization.*;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Asynchronous I/O implementation of echo protocol
 * @version 0.1
 */
public class InstaYakAIOHandler implements AIOHandler {
	
	/**
	 * whether or not entire message was received
	 */
	private boolean entireMessageReceived = false;
	
	/**
	 * buffers of current message
	 */
	private ArrayList<byte[]> buffs = new ArrayList<>();
	
	/**
	 * Whether or not client has been authenticated
	 */
	private boolean authenticated = false;
	
	/**
	 * the user that is being handled
	 */
	private User user = null;
	
	/**
	 * the nonce that was sent to the client
	 */
	private String nonce;
	
	/**
	 * twitter4j client
	 */
	private Twitter twitter;
	
	/**
	 * list of users that were in credentials file
	 */
	private ArrayList<User> users;
	
	/**
	 * Constructs a new InstaYakAIOHandler
	 * @param passwordFile location of password file
	 */
	public InstaYakAIOHandler(String passwordFile) {
		this.users = getUserList(passwordFile);
		twitter = new TwitterFactory().getInstance();
	}
	
	/**
	 * Sends an InstaYakVersion message to the client
	 */
	@Override
	public byte[] handleAccept() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(os);
        InstaYakVersion version = new InstaYakVersion();
        
        try {
			version.encode(out);
		} catch (IOException e) {
		}
        
		return os.toByteArray();
	}
	
	/* (non-Javadoc)
	 * @see AIOHandler#handleRead(byte[])
	 */
	@Override
	public byte[] handleRead(byte[] readBuff) {

		buffs.add(readBuff);
		
		if (readBuff[readBuff.length - 2] != '\r' || readBuff[readBuff.length - 1] != '\n') {
			return null;
		} else {
			
			
			int length = 0;
			for (byte[] buff : buffs) {
				length += buff.length;
			}
			
			int ndx = 0;
			byte[] completeBuff = new byte[length - 2];
			
			for (int i = 0; i < buffs.size() - 1; i++) {
				for (byte b : buffs.get(i)) {
					completeBuff[ndx] = b;
					ndx++;
				}
			}
				
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			InputStream is = new ByteArrayInputStream(completeBuff);
			
			MessageInput in = new MessageInput(is);
	        MessageOutput out = new MessageOutput(os);
	        
	        InstaYakMessage response = null;
	        
	        try {
				InstaYakMessage message = InstaYakMessage.decode(in);
				
				System.out.println("op: " + message.getOperation());
				
				if (!authenticated) {
					if (user == null) {
						
						// wait for client to respond with an InstaYakID message
						InstaYakID iyid = (InstaYakID) message;
						
						// see if there are any users with the given ID
						user = findUser(iyid.getID(), users);
						
						// if no users were found then send/log an error and terminate
						if (user == null) {
							response = new InstaYakError("No such user " + iyid.getID());
						} else {
							// generate a random nonce to use for an InstaYakChallenge message
							nonce = generateNonce();
							response = new InstaYakChallenge(nonce);
						}
						
					} else {
						
						InstaYakCredentials creds = (InstaYakCredentials) message;
						String hash = creds.getHash();
						
						// check if InstaYakCredentials message had a valid hash or not
						if (!validateHash(hash, user.getPassword(), nonce)) {
							response = new InstaYakError("Unable to authenticate");
						} else {
							authenticated = true;
							response = new InstaYakACK();
						}
						
					}
				} else {
					
					response = new InstaYakACK();
					
					// read buff should contain either a InstaYakUOn or an InstaYakSLMD
					if (message.getOperation() == InstaYakUOn.operation) {
						handleUOn((InstaYakUOn) message);
					} else if (message.getOperation() == InstaYakSLMD.operation) {
						sendSLMD();
					} else {
						response = new InstaYakError("");
					}
					
				}
				
				buffs.clear();
				
				response.encode(out);
				
	        } catch (Exception e) {
	        	System.err.println(e.getMessage());
	        	//e.printStackTrace();
			}
	        
			return os.toByteArray();
		
		}
		
		/*System.out.println(new String(readBuff));
			
		return readBuff;*/
	}
	
	/**
	 * Sends a UOn message to twitter
	 * 
	 * @param uon the InstaYakUOn message to post to twitter
	 * @throws TwitterException if twitter 4j has an issue
	 */
	public void handleUOn(final InstaYakUOn uon) throws TwitterException {
		updateStatus(twitter, user.getId() + ": UOn #" + uon.getCategory(), uon.getImage());
	}
	
	/**
	 * Posts a SLMD message to twitter
	 * 
	 * @throws TwitterException if twitter4j has an issue
	 */
	public void sendSLMD() throws TwitterException {
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
	
	/**
	 * Reads each row of password file and initializes a new User object which it then
	 * inserts in to an ArrayList of Users. Once all users have been added the arraylist
	 * is returned.
	 * 
	 * @param passwordFileLocation location of password file to iterate over
	 * @return list of all users in password file
	 */
	public static ArrayList<User> getUserList(final String passwordFileLocation) {
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
	 * Searches for a user in the user list by id
	 * 
	 * @param id value to search for
	 * @param users list of users to search through
	 * @return user with given id or null if not found
	 */
	public static User findUser(final String id, final ArrayList<User> users) {
		
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
	private static boolean validateHash(final String hash, final String password, final String nonce) {
		
		String computedHash = "";
		
		try {
			computedHash = ComputeHash.computeHash(nonce + password);
		} catch (UnsupportedEncodingException e) {
			System.out.println("unsupported encoding exception");
		}
		
		return hash.equals(computedHash);
	}
	
}
