/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 0
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Represents an InstaYak challenge and provides serialization/deserialization
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakChallenge extends InstaYakMessage {

	/**
	 * unique identifier of this operation
	 */
    public static final String operation = "CLNG";

    /**
     * regex expression that tests if a string matches the format of an
     * InstaYakChallenge message
     */
    public static String pattern = "^CLNG [0-9]+$";

    /**
     * regex expression that test for valid nonces
     */
    private final String noncePattern = "^[0-9]+$";

    private String nonce = "";

    /**
     * Constructs challenge message using given values
     *
     * @param nonce challenge nonce
     * @throws InstaYakException if validation fails
     */
    public InstaYakChallenge(String nonce) throws InstaYakException {
        this.setNonce(nonce);
    }

    /**
     * Constructs challenge message using deserialization. Only parses material
     * specific to this message (that is not
     * operation)
     *
     * @param in deserialization input source
     * @throws IOException if there are no messages to be read
     * @throws InstaYakException if message is not in correct format
     */
    public InstaYakChallenge(MessageInput in)
            throws InstaYakException, IOException {

        // check if in has a message to be read
    	if (in.getLast() == null) {
        	if (!in.hasNext()) {
        		throw new IOException();
        	}
        	in.popNext();
        }

        // check if next message is a valid InstaYakChallenge message
        if (!Pattern.matches(pattern, in.getLast())) {
            throw new InstaYakException("Incorrect message format");
        }

        // read next message and remove it from message list
        String message = in.popLast();

        // extract server nonce and store it
        setNonce(message.substring(5));

    }

    /**
     * Returns a String representation ("Challenge: Nonce=12345")
     *
     * @return string representation
     */
    public String toString(){
        return "Challenge: Nonce=" + getNonce();
    }

    /**
     * Returns nonce
     *
     * @return nonce
     */
    public final String getNonce() {
        return nonce;
    }

    /**
     * Sets nonce
     *
     * @param nonce new nonce
     * @throws InstaYakException if invalid nonce
     * @throws NullPointerException if null nonce
     */
    public void setNonce(String nonce) throws InstaYakException {
        
    	// make sure nonce is not null
        if (nonce == null) {
        	throw new InstaYakException("Null nonce given");
        }
        // make sure nonce is one or more numeric characters
        else if (!Pattern.matches(noncePattern, nonce)) {
            throw new InstaYakException("Invalid ID");
        }
        else {
            this.nonce = nonce;
        }
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    public String getOperation() { return operation; }

    /**
     * Serializes message to given output sink
     *
     * @param out serialization output sink
     * @throws IOException if I/O problem
     */
    public void encode(MessageOutput out) throws IOException {
        out.writeMessage(getOperation() + " " + getNonce());
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nonce == null) ? 0 : nonce.hashCode());
		result = prime * result + ((noncePattern == null) ? 0 : noncePattern.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstaYakChallenge other = (InstaYakChallenge) obj;
		if (nonce == null) {
			if (other.nonce != null)
				return false;
		} else if (!nonce.equals(other.nonce))
			return false;
		if (noncePattern == null) {
			if (other.noncePattern != null)
				return false;
		} else if (!noncePattern.equals(other.noncePattern))
			return false;
		return true;
	}


}
