/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 0
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Serialization output sink
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class MessageOutput {

    private static final String MESSAGE_DELIMITER = "\r\n";
    private OutputStream out;

    /**
     * Constructs a new output sink from an OutputStream
     *
     * @param out byte output sink
     */
    public MessageOutput(OutputStream out) throws NullPointerException {

        if (out == null) {
            throw new NullPointerException();
        }

        this.out = out;
    }

    /**
     * Writes a message to the output stream by appending "\r\n" and converting
     * it from a string to a byte array
     *
     * @param message message to be sent
     * @throws IOException if message cannot be converted to a byte array
     */
    public void writeMessage(String message) throws IOException {

        byte[] messageBytes;
        byte[] delimiterBytes;

        // Convert string to byte array
        try {
            messageBytes = message.getBytes("ISO-8859-1");
            delimiterBytes = MessageOutput.MESSAGE_DELIMITER.getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            throw new IOException();
        }

        // write byte arrays for message and delimiter to output stream
        this.out.write(messageBytes);
        this.out.write(delimiterBytes);
    }

}
