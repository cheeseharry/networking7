/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 1
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Represents a InstaYak credentials and provides serialization/deserialization
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakCredentials extends InstaYakMessage {

	/**
	 * unique identifier of this operation
	 */
    public final static String operation = "CRED";

    /**
     * regex expression that tests if a string matches the format of an
     * InstaYakCredentials message
     */
    public static String pattern = "^CRED [A-F0-9]{32}$";

    /**
     * regex expression that matches valid hashes
     */
    private final String hashPattern = "^[A-F0-9]{32}$";

    private String hash;

    /**
     * Constructs credentials message using given hash
     *
     * @param hash hash for credentials
     * @throws InstaYakException if validation of hash fails
     */
    public InstaYakCredentials(String hash) throws InstaYakException {
        this.setHash(hash);
    }

    /**
     * Constructs credentials message using deserialization. Only parses
     * material specific to this message (that is not operation)
     *
     * @param in deserialization input source
     * @throws IOException if there are no messages to be read
     * @throws InstaYakException if message is not in correct format
     */
    public InstaYakCredentials(MessageInput in)
            throws InstaYakException, java.io.IOException {

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
        setHash(message.substring(5));

    }

    /**
     * Returns hash
     *
     * @return hash
     */
    public final String getHash() {
        return hash;
    }

    /**
     * Sets hash
     *
     * @param hash new hash
     * @throws InstaYakException if null or invalid hash
     */
    public final void setHash(String hash) throws InstaYakException {
        // make sure hash is exactly 32 numeric characters or uppercase A-F
        // characters
        if (hash == null) {
            throw new InstaYakException("null hash");
        } else if(!Pattern.matches(hashPattern, hash)) {
            throw new InstaYakException("Invalid Hash");
        }
        else {
            this.hash = hash;
        }
    }

    /**
     * Returns a String representation ("Credentials: Hash=12345")
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Credentials: Hash=" + getHash();
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @Override
    public String getOperation() { return operation; }

    /**
     * Serializes message to given output sink
     *
     * @param out serialization output sink
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        out.writeMessage(getOperation() + " " + getHash());
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((hashPattern == null) ? 0 : hashPattern.hashCode());
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("static-access")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstaYakCredentials other = (InstaYakCredentials) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (hashPattern == null) {
			if (other.hashPattern != null)
				return false;
		} else if (!hashPattern.equals(other.hashPattern))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		return true;
	}


}
