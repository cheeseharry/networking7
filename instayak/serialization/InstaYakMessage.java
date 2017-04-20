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
 * Represents generic portion of InstaYak message and provides
 * serialization/deserialization You may make abstract anything listed in
 * this interface you wish as part of good design. In other words, this API
 * only specifies class name and method signatures, nothing more.
 *
 * @version 1.0
 * @author Kevin Aud
 */
public abstract class InstaYakMessage {

    /**
     * De-serializes message from input source
     *
     * @param in deserialization input source
     * @return a specific InstaYak message resulting from deserialization
     * @throws InstaYakException if parse or validation problem
     * @throws IOException if I/O problem
     */
    public static InstaYakMessage decode(MessageInput in)
            throws InstaYakException, IOException {

        // check if in has anymore messages
        /*if (!in.hasNext()) {
            throw new IOException("no message to be read from message input");
        }*/
    	String message = in.popNext();

        // Test if next message is an InstaYakVersion message
        if (Pattern.matches(InstaYakVersion.pattern, message)) {
            return new InstaYakVersion(in);

            // Test if next message is an InstaYakID message
        } else if (Pattern.matches(InstaYakID.pattern, message)) {
            return new InstaYakID(in);

            // Test if next message is an InstaYakChallenge message
        } else if (Pattern.matches(InstaYakChallenge.pattern, message)) {
            return new InstaYakChallenge(in);

        } else if (Pattern.matches(InstaYakACK.pattern, message)) {
            return new InstaYakACK(in);

        } else if (Pattern.matches(InstaYakSLMD.pattern, message)) {
            return new InstaYakSLMD(in);

        } else if (Pattern.matches(InstaYakCredentials.pattern, message)) {
            return new InstaYakCredentials(in);

        } else if (Pattern.matches(InstaYakError.pattern, message)) {
            return new InstaYakError(in);

        } else if (Pattern.matches(InstaYakUOn.pattern, message)) {
            return new InstaYakUOn(in);

        } else {
            // did not match any InstaYak message formats
            throw new InstaYakException("unknown message format");
        }

    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    public abstract String getOperation();

    /**
     * Serializes message to given output sink
     *
     * @param out serialization output sink
     * @throws IOException if I/O problem
     */
    public abstract void encode(MessageOutput out) throws IOException;

}
