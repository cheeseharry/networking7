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
 * Represents an InstaYak ID and provides serialization/deserialization
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakID extends InstaYakMessage {

	/**
	 * unique identifier of this operation
	 */
    public final static String operation = "ID";

    /*http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=472922*https://marketplace.eclipse.org/content/eclipse-class-decompiler
     * regex expression that tests if a string matches the format of an
     * InstaYakID message
     */
    public static String pattern = "^ID [a-zA-Z0-9]+$";

    /**
     * regex expression that tests for a valid ID
     */
    private final String idPattern = "^[a-zA-z0-9]+$";

    /**
     * stores ID once it is extracted from stream or passed into constructor
     */
    private String ID = "";

    /**
     * Constructs ID message using deserialization. Only parses material
     * specific to this message (that is not operation)
     *
     * @param in deserialization input source
     * @throws IOException if there are no messages to be read
     * @throws InstaYakException if message is not in correct format
     */
    public InstaYakID(MessageInput in) throws InstaYakException, IOException {

        // check if in has a message to be read
    	if (in.getLast() == null) {
        	if (!in.hasNext()) {
        		throw new IOException();
        	}
        	in.popNext();
        }

        // check if next message is a valid InstaYakID message
        if (!Pattern.matches(pattern, in.getLast())) {
            throw new InstaYakException("Incorrect message format");
        }

        // read next message and remove it from message list
        String message = in.popLast();

        // extract ID and store it
        setID(message.substring(3));
    }

    /**
     * Constructs ID message using set values
     *
     * @param ID ID for user
     * @throws InstaYakException if validation fails
     */
    public InstaYakID(String ID) throws InstaYakException {
        // store ID
        setID(ID);
    }

    /**
     * Serializes message to given output sink
     *
     * @param out serialization output sink
     * @throws IOException if I/O problem
     */
    public void encode(MessageOutput out) throws IOException {
        out.writeMessage(getOperation() + " " + getID());
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Returns ID
     *
     * @return ID
     */
    public final String getID() {
        return ID;
    }

    /**
     * Sets ID
     *
     * @param ID new ID
     * @throws InstaYakException if ID is null or invalid
     */
    public final void setID(String ID) throws InstaYakException {
    	
    	// make sure ID is not null
    	if (ID == null) {
    		throw new InstaYakException("null ID");
    	}
        // make sure ID is one or more alphanumeric characters
    	else if(!Pattern.matches(idPattern, ID)) {
            throw new InstaYakException("Invalid ID");
        }
        else {
            this.ID = ID;
        }
    }

    /**
     * Returns a String representation ("ID: ID=bob")
     *
     * @return string representation
     */
    public String toString() {
        return "ID: ID=" + getID();
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((idPattern == null) ? 0 : idPattern.hashCode());
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
		InstaYakID other = (InstaYakID) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (idPattern == null) {
			if (other.idPattern != null)
				return false;
		} else if (!idPattern.equals(other.idPattern))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		return true;
	}

}
