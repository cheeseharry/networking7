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
 * Represents a InstaYak SLMD messages and provides
 * serialization/deserialization
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakSLMD extends InstaYakMessage {

	/**
	 * unique identifier of this operation
	 */
    public final static String operation = "SLMD";

    /**
     * regex expression that tests if a string matches the format of an
     * InstaYakSLMD message
     */
    public static String pattern = "^SLMD$";

    /**
     * Constructs SLMD message
     */
    public InstaYakSLMD() { }

    /**
     * Constructs new ACK message using deserialization. Only parses material
     * specific to this message (that is not operation)
     *
     * @param in deserialization input source
     * @throws IOException if there are no messages to be read
     * @throws InstaYakException if message is not in correct format
     */
    public InstaYakSLMD(MessageInput in)
            throws IOException, InstaYakException {

        // check if in has a message to be read
    	if (in.getLast() == null) {
        	if (!in.hasNext()) {
        		throw new IOException();
        	}
        	in.popNext();
        }

        // check if next message is a valid InstaYakChallenge message
        if (!Pattern.matches(InstaYakSLMD.pattern, in.getLast())) {
            throw new InstaYakException("Incorrect message format");
        }

        in.popLast();
    }

    /**
     * Returns a String representation ("SLMD")
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return getOperation();
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @SuppressWarnings("static-access")
	@Override
    public String getOperation() {
        return this.operation;
    }

    /**
     * Serializes message to given output sink
     *
     * @param out serialization output sink
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        out.writeMessage(getOperation());
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		InstaYakSLMD other = (InstaYakSLMD) obj;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		return true;
	}

    
}
