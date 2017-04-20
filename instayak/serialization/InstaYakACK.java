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
 * Represents an InstaYak ACK and provides serialization/deserialization
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakACK extends InstaYakMessage {

	/**
	 * unique identifier of this operation
	 */
    public static final String operation = "ACK";

    /**
     * regex expression that tests if a string matches the format of an
     * InstaYakACK message
     */
    public static String pattern = "^ACK$";

    /**
     * Constructs ACK message
     */
    public InstaYakACK() { }

    /**
     * Constructs new ACK message using deserialization. Only parses material
     * specific to this message (that is not operation)
     *
     * @param in deserialization input source
     * @throws IOException if there are no messages to be read
     * @throws InstaYakException if message is not in correct format
     */
    public InstaYakACK(MessageInput in)
                throws IOException, InstaYakException {

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

        // remove next message from message list
        in.popLast();

    }

    /**
     * Returns a String representation ("ACK")
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
        out.writeMessage("ACK");
    }

    @SuppressWarnings("static-access")
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstaYakACK that = (InstaYakACK) o;

        return operation.equals(that.operation);
    }

    @Override
    public int hashCode() {
        return operation.hashCode();
    }

}
