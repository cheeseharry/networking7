/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 1
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization.test;

import org.junit.Test;

import instayak.serialization.*;

import java.io.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class InstaYakErrorTest {

    private static final byte[] encoding;

    static {
        try {
            encoding = "ERROR error 1\r\n".getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode", e);
        }
    }

    /**
     * encode should output a correctly formatted InstaYakError message to the
     * MessageOutput it is given
     */
    @Test
    public void testEncode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput mout = new MessageOutput(bout);
        try {
			new InstaYakError("error 1").encode(mout);
		} catch (IOException | InstaYakException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertArrayEquals(encoding, bout.toByteArray());
    }

    /**
     * decode should determine that the message is an InstaYakError message and pass
     * back a new InstaYakError with the proper message stored
     */
    @Test
    public void testDecodeMessageInput() {
        MessageInput min = new MessageInput(new ByteArrayInputStream(encoding));
        InstaYakError iye;
		try {
			iye = (InstaYakError) InstaYakError.decode(min);
	        assertEquals("Error: Message=error 1", iye.toString());
		} catch (InstaYakException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor should properly parse the error message from a MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructorTest1() throws Exception {
        String input = "ERROR Error 1\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

        //assertEquals(true, mi.hasNext());

        InstaYakError iye = new InstaYakError(mi);

       // assertEquals(false, mi.hasNext());
        assertEquals("Error: Message=Error 1", iye.toString());
    }

    /**
     * Constructor should throw an InstaYakException if the next message is
     * not a valid InstaYakError message
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void messageInputConstructorTest2() throws Exception {

        // this in an InstaYakCredentials message not an InstaYakError message
        String input = "CRED 000102030405060708090A0B0C0D0E0F\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

       // assertEquals(true, mi.hasNext());

        @SuppressWarnings("unused")
		InstaYakError iye = new InstaYakError(mi);
    }

    /**
     * Constructor should throw an IOException if there is no next message in
     * the message input
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=IOException.class)
    public void messageInputConstructorTest3() throws Exception {
        String input = "ERROR Error 1\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

       // assertEquals(true, mi.hasNext());
        mi.popNext();
       // assertEquals(false, mi.hasNext());

        @SuppressWarnings("unused")
		InstaYakError iye = new InstaYakError(mi);
		iye = new InstaYakError(mi);
    }

    /**
     * Constructor should properly store the message it is passed as an input
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageConstructorTest1() throws Exception {
        InstaYakError iye = new InstaYakError("Error 1");
        assertEquals("Error: Message=Error 1", iye.toString());
    }

    /**
     * Constructor should throw an instayak exception if the message it is given is not valid
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void messageConstructorTest2() throws Exception {
        // not valid because it contains asterisks
        @SuppressWarnings("unused")
		InstaYakError iye = new InstaYakError("12345**");
    }

    /**
     * getMessage should return the message
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getMessageTest1() throws Exception {
        InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0F");
        assertEquals("000102030405060708090A0B0C0D0E0F", iyc.getHash());
    }

    /**
     * setMessage should update the message
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void setMessageTest1() throws Exception {
        InstaYakError iye = new InstaYakError("Error 1");
        assertEquals("Error 1", iye.getMessage());
        iye.setMessage("Error 2");
        assertEquals("Error 2", iye.getMessage());
    }

    /**
     * setMessage should throw an InstaYakException if the message that is passed
     * in is not in the valid format
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setMessageTest2() throws Exception {
        InstaYakError iye = new InstaYakError("Error 1");
        assertEquals("Error 1", iye.getMessage());
        // contains asterisks so is not a valid error message
        iye.setMessage("***");
    }

    /**
     * setMessage should throw an InstaYakException if the message that is passed
     * in is null
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setMessageTest3() throws Exception {
        InstaYakError iye = new InstaYakError("Error 1");
        assertEquals("Error 1", iye.getMessage());
        iye.setMessage(null);
    }

    /**
     * toString should return a string in the format "Error: Message=(message)"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void toStringTest1() throws Exception {
        InstaYakError iye = new InstaYakError("Error 1");
        assertEquals("Error: Message=Error 1", iye.toString());
        iye.setMessage("Error 2");
        assertEquals("Error: Message=Error 2", iye.toString());
    }

    /**
     * getOperation should return ERROR
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getOperation() throws Exception {
        InstaYakError iye = new InstaYakError("Error 1");
        assertEquals("ERROR", iye.getOperation());
    }

    /**
     * pattern should be a regex expression that only accepts messages of the
     * format "ERROR (message)" where (message) is 1+ alphanumeric characters
     * or space characters
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void regexPatternTest() throws Exception {

        // Valid patterns
        assertTrue(Pattern.matches(InstaYakError.pattern,
                "ERROR error message"));
        assertTrue(Pattern.matches(InstaYakError.pattern,"ERROR message"));
        assertTrue(Pattern.matches(InstaYakError.pattern,"ERROR 1"));
        assertTrue(Pattern.matches(InstaYakError.pattern,"ERROR Some Message 1"));
        assertTrue(Pattern.matches(InstaYakError.pattern,"ERROR  "));

        // Invalid patterns
        assertFalse(Pattern.matches(InstaYakError.pattern,"error message"));
        assertFalse(Pattern.matches(InstaYakError.pattern,"ERROR"));
        assertFalse(Pattern.matches(InstaYakError.pattern,"ERROR message*"));
        assertFalse(Pattern.matches(InstaYakError.pattern,"XERROR message"));
        assertFalse(Pattern.matches(InstaYakError.pattern,"ERRO message"));
    }

}