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
 * Represents a InstaYak version messages and provides
 * serialization/deserialization
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakVersion extends InstaYakMessage {

	/**
	 * unique identifier of this operation
	 */
    public final static String operation = "INSTAYAK";

    /**
     * InstaYakVersion string representation
     */
    private final String stringRep = "InstaYak";

    /**
     * regex expression that tests if a string matches the format of an
     * InstaYakVersion message
     */
    public static String pattern = "^INSTAYAK [0-9]+.[0-9]+$";

    /**
     * regex expression that test if a version number is in the correct format
     */
    private String versionPattern = "^[0-9]+.[0-9]+$";

    /**
     * stores version number
     */
    private String version = "1.0";

    /**
     * Constructs version message with default version number, 1.0
     */
    public InstaYakVersion() { }

    /**
     * Constructs version message using deserialization. Only parses material
     * specific to this message (that is not operation)
     *
     * @param in deserialization input source
     * @throws IOException if there are no messages to be read
     * @throws InstaYakException if message is not in correct format
     */
    public InstaYakVersion(MessageInput in) throws InstaYakException, IOException {

        // check if in has a message to be read
    	if (in.getLast() == null) {
        	if (!in.hasNext()) {
        		throw new IOException();
        	}
        	in.popNext();
        }

        // check if next message is a valid InstaYakVersion message
        if (!Pattern.matches(InstaYakVersion.pattern, in.getLast())) {
            throw new InstaYakException("Incorrect message format");
        }

        // read next message and remove it from message list
        String message = in.popLast();

        // extract version number and store it
        this.setVersion(message.substring(9));

    }

    /**
     * Returns a String representation ("InstaYak")
     *
     * @return string representation
     */
    public String toString() {
        return stringRep;
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
        out.writeMessage(getOperation() + " " + getVersion());
    }

    /**
     * returns version number
     *
     * @return version number
     */
    public String getVersion() {
        return version;
    }

    /**
     * sets version number if it is valid
     *
     * @param version version number to initialize with
     * @throws NullPointerException if given null value for version
     * @throws InstaYakException if message is not in correct format
     */
    public void setVersion(String version)
            throws InstaYakException, NullPointerException {

        // make sure version is not null
        if (version == null) {
            throw new NullPointerException();
        }
        // make sure version is in valid format
        if(!Pattern.matches(versionPattern, version)) {
            throw new InstaYakException("Invalid Version");
        }

        this.version = version;

    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((stringRep == null) ? 0 : stringRep.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((versionPattern == null) ? 0 : versionPattern.hashCode());
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
		InstaYakVersion other = (InstaYakVersion) obj;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (stringRep == null) {
			if (other.stringRep != null)
				return false;
		} else if (!stringRep.equals(other.stringRep))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (versionPattern == null) {
			if (other.versionPattern != null)
				return false;
		} else if (!versionPattern.equals(other.versionPattern))
			return false;
		return true;
	}

}
