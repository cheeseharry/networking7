/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 0
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization;

import java.io.IOException;
import java.io.InputStream;

/**
 * Deserialization input source
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class MessageInput {


    private static final String MESSAGE_DELIMITER = "\r\n";
    private InputStream in;
    private String lastMessage = null;

    /**
     * Constructs a new input source from an InputStream
     *
     * @param in byte input source
     * @throws NullPointerException if in is null
     */
    public MessageInput(InputStream in) throws NullPointerException {

        // make sure that in exists
        if (in == null) {
            throw new NullPointerException();
        }
        
        this.in = in;

    }

    /**
     * returns next message without removing it from message list
     *
     * @return next message
     * @throws InstaYakException if there are no more messages
     */
    public String getLast() throws InstaYakException {
        return lastMessage;
    }
    
    public String popLast() {
    	String message = lastMessage;
    	lastMessage = null;
    	return message;
    }

    /**
     * returns next message and removes it from message list
     *
     * @return next message
     * @throws InstaYakException if there are no more messages
     */
    public String popNext() throws InstaYakException {
    	
    	String message = "";
    
        byte[] buffer = new byte[1];
        boolean endOfMessage = false;

        try {
        	
            // convert input stream to a string one byte at a time
            while (!endOfMessage && in.read(buffer) != -1) {
                message += new String(buffer, "ISO-8859-1");
                if (message.length() >= 2 && message.substring(message.length() - 2).equals(MessageInput.MESSAGE_DELIMITER)) {
                	endOfMessage = true;
                	message = message.substring(0, message.length() - 2);
                }
            }

        }
        catch (IOException e) {
        	throw new InstaYakException("unknown io exception", e);
        }
        
	    lastMessage = message;
        return message;
    }

    /**
     * indicates whether or not there are any more messages
     *
     * @return true if there are one or more messages, false otherwise
     */
    public boolean hasNext() {
    	boolean next = false;
        try {
			next = in.available() != 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return next;
    }
}




















