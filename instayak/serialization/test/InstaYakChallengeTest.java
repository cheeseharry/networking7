/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 0
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization.test;

import org.junit.Test;

import instayak.serialization.*;

import java.io.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class InstaYakChallengeTest {

    private static final byte[] encoding;

    static {
        try {
            encoding = "CLNG 1234\r\n".getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode", e);
        }
    }

    /**
     * encode should output a correctly formatted Challenge message to the
     * MessageOutput it is given
     */
    @Test
    public void testEncode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput mout = new MessageOutput(bout);
        try {
			new InstaYakChallenge("1234").encode(mout);
		} catch (IOException | InstaYakException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertArrayEquals(encoding, bout.toByteArray());
    }

    /**
     * decode should determine that the message is a Challenge message and pass
     * back a new InstaYakChallenge with the proper nonce stored
     */
    @Test
    public void testDecodeMessageInput() {
        MessageInput min = new MessageInput(new ByteArrayInputStream(encoding));
        InstaYakChallenge iyc;
		try {
			iyc = (InstaYakChallenge) InstaYakMessage.decode(min);
	        assertEquals("1234", iyc.getNonce());
		} catch (InstaYakException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor should properly parse the nonce from a MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructorTest1() throws Exception {
        String input = "CLNG 1234\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

       // assertEquals(true, mi.hasNext());

        InstaYakChallenge iyc = new InstaYakChallenge(mi);

       // assertEquals(false, mi.hasNext());
        assertEquals("1234", iyc.getNonce());
    }

    /**
     * Constructor should throw an InstaYakException if it is given a
     * MessageInput that does not have a Challenge message
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void messageInputConstructorTest2() throws Exception {
        String input = "INSTAYAK 1.0\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

        //assertEquals(true, mi.hasNext());

        @SuppressWarnings("unused")
		InstaYakChallenge iyc = new InstaYakChallenge(mi);
    }

    /**
     * Constructor should throw an InstaYakException if it is given an
     * invalid nonce
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void nonceConstructorTest1() throws Exception {
        @SuppressWarnings("unused")
		InstaYakChallenge iyc = new InstaYakChallenge("---");
    }

    /**
     * Constructor should store the nonce that is passed into it if the nonce
     * is valid
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void nonceConstructorTest2() throws Exception {
        InstaYakChallenge iyc = new InstaYakChallenge("1234");
        assertEquals("1234", iyc.getNonce());
    }

    /**
     * setNonce should update the stored nonce if it is given a valid nonce
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void setNonceTest1() throws Exception {
        InstaYakChallenge iyc = new InstaYakChallenge("1234");
        iyc.setNonce("5678");
        assertEquals("5678", iyc.getNonce());
    }

    /**
     * setNonce should throw an InstaYakException if it is given an invalid
     * nonce
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setNonceTest2() throws Exception {
        InstaYakChallenge iyc = new InstaYakChallenge("1234");
        iyc.setNonce("---");
    }

    /**
     * toString should return a string in the format "Challenge: Nonce=(Nonce)"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void toStringTest1() throws Exception {
        InstaYakChallenge iyc = new InstaYakChallenge("1234");
        assertEquals("Challenge: Nonce=1234", iyc.toString());
    }

    /**
     * getOperation should return CLNG
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getOperation() throws Exception {
        InstaYakChallenge iyc = new InstaYakChallenge("1234");
        assertEquals("CLNG", iyc.getOperation());
    }

    /**
     * pattern should be a regex expression that only accepts messages of the
     * format "CLNG (servnonce)" where (servnonce) is one or more
     * numeric characters
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void regexPatternTest() throws Exception {
        assertTrue(Pattern.matches(InstaYakChallenge.pattern, "CLNG 1"));
        assertFalse(Pattern.matches(InstaYakChallenge.pattern, "CLNG 1.0"));
        assertFalse(Pattern.matches(InstaYakChallenge.pattern, "CLNG 1a"));
        assertFalse(Pattern.matches(InstaYakChallenge.pattern, "CLNG 1\r\n"));
        assertFalse(Pattern.matches(InstaYakChallenge.pattern, " CLNG 1"));
        assertFalse(Pattern.matches(InstaYakChallenge.pattern, "CLNG"));
        assertTrue(Pattern.matches(InstaYakChallenge.pattern, "CLNG 1123456481235773974682"));
        assertFalse(Pattern.matches(InstaYakChallenge.pattern, "CLNG 1123456481235773974682 x"));
    }

}