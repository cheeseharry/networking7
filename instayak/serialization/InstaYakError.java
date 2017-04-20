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
* Represents an InstaYak error and provides serialization/deserialization
*
* @version 1.0
* @author Kevin Aud
*/
public class InstaYakError extends InstaYakMessage {

	/**
	 * unique identifier of this operation
	 */
   public static final String operation = "ERROR";

   /**
    * regex expression that tests if a string matches the format of an
    * InstaYakError message
    */
   public static String pattern = "^ERROR [a-zA-Z0-9\\s]+$";

   /**
    * regex expression that tests for valid error messages
    */
   private final String messagePattern = "^[a-zA-Z0-9\\s]+$";

   /**
    * used to store message once it is extracted from stream or passed into
    * constructor
    */
   private String message = "";

   /**
    * Constructs error message using set values
    *
    * @param message error message
    * @throws InstaYakException if validation fails
    */
   public InstaYakError(String message) throws InstaYakException {
       setMessage(message);
   }

   /**
    * Constructs error message using deserialization. Only parses material
    * specific to this message (that is not operation)
    *
    * @param in deserialization input source
     * @throws IOException if there are no messages to be read
     * @throws InstaYakException if message is not in correct format
    */
   public InstaYakError(MessageInput in)
           throws InstaYakException, IOException {

       // check if in has a message to be read
	   if (in.getLast() == null) {
       	if (!in.hasNext()) {
       		throw new IOException();
       	}
       	in.popNext();
       }

       // check if next message is a valid InstaYakError message
       if (!Pattern.matches(pattern, in.getLast())) {
           throw new InstaYakException("Incorrect message format");
       }

       // read next message and remove it from message list
       String message = in.popLast();

       // extract error message and store it
       setMessage(message.substring(6));

   }

   /**
    * Returns message
    *
    * @return message
    */
   public final String getMessage() {
       return message;
   }

   /**
    * Sets message
    *
    * @param message new message
    * @throws InstaYakException if null or invalid message
    */
   public final void setMessage(String message) throws InstaYakException {

       // make sure input is not null
       if (message == null) {
           throw new InstaYakException("null message");
       }

       // make sure message is one or more alphanumeric characters or spaces
       if(!Pattern.matches(messagePattern, message)) {
           throw new InstaYakException("Invalid message");
       }

       this.message = message;

   }

   /**
    * Returns a String representation ("Error: Message=Bad stuff")
    *
    * @return string representation
    */
   @Override
   public String toString() {
       return "Error: Message=" + getMessage();
   }

   /**
    * Returns message operation
    *
    * @return message operation
    */
   @Override
   public String getOperation() {
       return operation;
   }

   /**
    * Serializes message to given output sink
    *
    * @param out serialization output sink
    * @throws IOException if I/O problem
    */
   @Override
   public void encode(MessageOutput out) throws IOException {
       out.writeMessage(getOperation() + " " + getMessage());
   }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((messagePattern == null) ? 0 : messagePattern.hashCode());
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
		InstaYakError other = (InstaYakError) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (messagePattern == null) {
			if (other.messagePattern != null)
				return false;
		} else if (!messagePattern.equals(other.messagePattern))
			return false;
		return true;
	}


}
