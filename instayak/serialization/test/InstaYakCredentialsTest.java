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

public class InstaYakCredentialsTest {

    private static final byte[] encoding;

    static {
        try {
            encoding = "CRED 000102030405060708090A0B0C0D0E0F\r\n".getBytes("ISO8859-1");
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
			new InstaYakCredentials("000102030405060708090A0B0C0D0E0F").encode(mout);
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
        InstaYakCredentials iyc;
		try {
			iyc = (InstaYakCredentials) InstaYakMessage.decode(min);
			assertEquals("Credentials: Hash=000102030405060708090A0B0C0D0E0F", iyc.toString());  
		} catch (InstaYakException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor should properly parse the hash from a MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructorTest1() throws Exception {
        String input = "CRED 000102030405060708090A0B0C0D0E0F\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

        //assertEquals(true, mi.hasNext());

        InstaYakCredentials iyc = new InstaYakCredentials(mi);

        //assertEquals(false, mi.hasNext());
        assertEquals("Credentials: Hash=000102030405060708090A0B0C0D0E0F", iyc.toString());
    }

    /**
     * Constructor should throw an InstaYakException if the next message is
     * not a valid credentials message
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void messageInputConstructorTest2() throws Exception {
        // invalid hash because it contains a 'G'
        String input = "CRED 000102030405060708090A0B0C0D0E0G\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

        //assertEquals(true, mi.hasNext());

        @SuppressWarnings("unused")
		InstaYakCredentials iyc = new InstaYakCredentials(mi);
    }

    /**
     * Constructor should throw an IOException if there is no next message in
     * the message input
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=IOException.class)
    public void messageInputConstructorTest3() throws Exception {
        String input = "CRED 000102030405060708090A0B0C0D0E0F\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

        //assertEquals(true, mi.hasNext());
        mi.popNext();
        //assertEquals(false, mi.hasNext());

        @SuppressWarnings("unused")
		InstaYakCredentials iyc = new InstaYakCredentials(mi);
        iyc = new InstaYakCredentials(mi);
    }

    /**
     * Constructor should properly store the hash it is passed as an input
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void hashConstructorTest1() throws Exception {
        InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0F");
        assertEquals("Credentials: Hash=000102030405060708090A0B0C0D0E0F", iyc.toString());
    }

    /**
     * Constructor should throw an instayak exception if the hash string it is given is not valid
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void hashConstructorTest2() throws Exception {
        // not valid because it contains a G
        @SuppressWarnings("unused")
		InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0G");
    }

    /**
     * getHash should return the hash
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getHashTest1() throws Exception {
        InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0F");
        assertEquals("000102030405060708090A0B0C0D0E0F", iyc.getHash());
    }

    /**
     * setHash should update the hash
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void setHashTest1() throws Exception {
        InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0F");
        assertEquals("000102030405060708090A0B0C0D0E0F", iyc.getHash());
        iyc.setHash("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        assertEquals("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", iyc.getHash());
    }

    /**
     * setHash should throw an InstaYakException if the hash that is passed
     * in is not in the valid format
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setHashTest2() throws Exception {
        InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0F");
        assertEquals("000102030405060708090A0B0C0D0E0F", iyc.getHash());
        // contains a 'G' so it is not a valid hash
        iyc.setHash("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFG");
    }

    /**
     * setHash should throw an InstaYakException if the hash that is passed
     * in is null
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setHashTest3() throws Exception {
        InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0F");
        assertEquals("000102030405060708090A0B0C0D0E0F", iyc.getHash());
        iyc.setHash(null);
    }

    /**
     * toString should return a string in the format "Credentials: Hash=(hash)"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void toStringTest1() throws Exception {
        InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0F");
        assertEquals("Credentials: Hash=000102030405060708090A0B0C0D0E0F", iyc.toString());
        iyc.setHash("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        assertEquals("Credentials: Hash=FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", iyc.toString());
    }

    /**
     * getOperation should return CRED
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getOperation() throws Exception {
        InstaYakCredentials iyc = new InstaYakCredentials("000102030405060708090A0B0C0D0E0F");
        assertEquals("CRED", iyc.getOperation());
    }

    /**
     * pattern should be a regex expression that only accepts messages of the
     * format "CRED H((servnonce)(pwd))" where (servnonce) is 1+ numeric
     * characters and (pwd) is 0+ alphanumeric characters
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void regexPatternTest() throws Exception {

        // valid patterns
        assertTrue(Pattern.matches(InstaYakCredentials.pattern, "CRED 000102030405060708090A0B0C0D0E0F"));
        assertTrue(Pattern.matches(InstaYakCredentials.pattern, "CRED FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
        assertTrue(Pattern.matches(InstaYakCredentials.pattern, "CRED 00000000000000000000000000000000"));

        // invalid patterns
        assertFalse(Pattern.matches(InstaYakCredentials.pattern, "CRED 000102030405060708090A0B0C0D0E"));
        assertFalse(Pattern.matches(InstaYakCredentials.pattern, "CRED 000102030405060708090a0b0c0d0e0f"));
        assertFalse(Pattern.matches(InstaYakCredentials.pattern, "CRED 000102030405060708090A0B0C0D0E0G"));
        assertFalse(Pattern.matches(InstaYakCredentials.pattern, "000102030405060708090A0B0C0D0E0F"));
        assertFalse(Pattern.matches(InstaYakCredentials.pattern, "CRED"));
        assertFalse(Pattern.matches(InstaYakCredentials.pattern, "CRED 000102030405060708090A0B0C0D0E0F "));
        assertFalse(Pattern.matches(InstaYakCredentials.pattern, " CRED 000102030405060708090A0B0C0D0E0F"));
        assertFalse(Pattern.matches(InstaYakCredentials.pattern, "CRED  000102030405060708090A0B0C0D0E0F"));

    }

}