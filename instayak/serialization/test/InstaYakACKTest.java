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

public class InstaYakACKTest {

    InstaYakACK iya = new InstaYakACK();

    private static final byte[] encoding;

    static {
        try {
            encoding = "ACK\r\n".getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode", e);
        }
    }

    /**
     * encode should output a correctly formatted ACK message to the
     * MessageOutput it is given
     */
    @Test
    public void testEncode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput mout = new MessageOutput(bout);
        try {
			new InstaYakACK().encode(mout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertArrayEquals(encoding, bout.toByteArray());
    }

    /**
     * decode should determine that the message is an ACK message and pass
     * back a new InstaYakACK
     */
    @Test
    public void testDecodeMessageInput() {
        MessageInput min = new MessageInput(new ByteArrayInputStream(encoding));
        InstaYakACK iya;
		try {
			iya = (InstaYakACK) InstaYakACK.decode(min);
	        assertEquals("ACK", iya.toString());
		} catch (InstaYakException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor should properly parse the ACK from a MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructor1() throws Exception {

        String input = "ACK\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

        //assertEquals(true, in.getLast() == null);

        InstaYakACK iya = new InstaYakACK(mi);

       // assertEquals(false, mi.hasNext());
        assertEquals("ACK", iya.toString());

    }

    /**
     * toString should return a string in the format "ACK"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void toStringTest() throws Exception {
        String expected = "ACK";
        String actual = iya.toString();

        assertEquals(expected, actual);
    }

    /**
     * getOperation should return ACK
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getOperation() throws Exception {
        assertEquals("ACK", iya.getOperation());
    }

    /**
     * pattern should be a regex expression that only accepts messages of the
     * format "ACK"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void regexPatternTest() throws Exception {

        // Valid patterns
        assertTrue(Pattern.matches(InstaYakACK.pattern, "ACK"));

        // Invalid patterns
        assertFalse(Pattern.matches(InstaYakACK.pattern, "ACK\r\n"));
        assertFalse(Pattern.matches(InstaYakACK.pattern, " ACK "));
        assertFalse(Pattern.matches(InstaYakACK.pattern, "ack"));

    }

}