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

public class InstaYakIDTest {

    private static final byte[] encoding;

    static {
        try {
            encoding = "ID bob\r\n".getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode", e);
        }
    }

    /**
     * Constructor should properly parse the ID from a MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructorTest1() throws Exception {
        String input = "ID Bob\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

       // assertEquals(true, mi.hasNext());

        InstaYakID iyid = new InstaYakID(mi);

       // assertEquals(false, mi.hasNext());
        assertEquals("Bob", iyid.getID());
    }

    /**
     * Constructor should throw an IO Exception if it is given a MessageInput
     * that does not have anymore message to be read
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=IOException.class)
    public void messageInputConstructorTest2() throws Exception {
        String input = "ID Bob\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

       // assertEquals(true, mi.hasNext());

        mi.popNext();
       // assertEquals(false, mi.hasNext());

        @SuppressWarnings("unused")
		InstaYakID iyid = new InstaYakID(mi);
        iyid = new InstaYakID(mi);
    }

    /**
     * Constructor should throw an InstaYakException if it is given a
     * MessageInput that does not have an ID message
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void messageInputConstructorTest3() throws Exception {
        String input = "INSTAYAK 1.0\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

      //  assertEquals(true, mi.hasNext());

        @SuppressWarnings("unused")
		InstaYakID iyid = new InstaYakID(mi);
    }

    /**
     * Constructor should store the ID that is passed into it if the ID is valid
     */
    @Test
    public void IDConstructorTest1() {
        InstaYakID iyid;
		try {
			iyid = new InstaYakID("Bob");
	        assertEquals("Bob", iyid.getID());
		} catch (InstaYakException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor should throw an InstaYakException if it is given an
     * invalid ID
     * @throws InstaYakException if everything works properly
     */
    @Test(expected=InstaYakException.class)
    public void IDConstructorTest2() throws InstaYakException {
        @SuppressWarnings("unused")
		InstaYakID iyid = new InstaYakID("---");
    }

    /**
     * encode should output a correctly formatted ID message to the
     * MessageOutput it is given
     */
    @Test
    public void testEncode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput mout = new MessageOutput(bout);
        try {
			new InstaYakID("bob").encode(mout);
		} catch (IOException | InstaYakException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertArrayEquals(encoding, bout.toByteArray());
    }

    /**
     * decode should determine that the message is an ID message and pass
     * back a new InstaYakID with the proper ID stored
     */
    @Test
    public void testDecodeMessageInput() {
        MessageInput min = new MessageInput(new ByteArrayInputStream(encoding));
        InstaYakID idMsg;
		try {
			idMsg = (InstaYakID) InstaYakMessage.decode(min);
	        assertEquals("bob", idMsg.getID());
		} catch (InstaYakException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * setID should update the stored ID if it is given a valid ID
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void setIdTest1() throws Exception {
        InstaYakID iyid = new InstaYakID("Bob");
        iyid.setID("Kevin123");
        assertEquals("Kevin123", iyid.getID());
    }

    /**
     * setID should throw an InstaYakException if it is given an invalid ID
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setIdTest2() throws Exception {
        InstaYakID iyid = new InstaYakID("Bob");
        iyid.setID("---");
    }

    /**
     * getID should return the currently stored ID
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getIdTest1() throws Exception {
        InstaYakID iyid = new InstaYakID("Bob");
        assertEquals("Bob", iyid.getID());
    }

    /**
     * getOperation should return ID
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getOperation() throws Exception {
        InstaYakID iyid = new InstaYakID("Bob");
        assertEquals("ID", iyid.getOperation());
    }

    /**
     * toString should return a string in the format "ID: ID=(ID)"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void toStringTest() throws Exception {
        InstaYakID iyid = new InstaYakID("Bob");
        assertEquals("ID: ID=Bob", iyid.toString());
    }

    /**
     * pattern should be a regex expression that only accepts messages of the
     * format "ID (ID)" where (ID) is one or more alphanumeric characters
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void regexPattern1() throws Exception {
        assertTrue(Pattern.matches(InstaYakID.pattern, "ID xyz"));
        assertFalse(Pattern.matches(InstaYakID.pattern, "IDxyz"));
        assertFalse(Pattern.matches(InstaYakID.pattern, " ID xyz"));
        assertTrue(Pattern.matches(InstaYakID.pattern, "ID 1234"));
        assertTrue(Pattern.matches(InstaYakID.pattern, "ID 1234xys123scsf"));
        assertFalse(Pattern.matches(InstaYakID.pattern, "ID xy,.z"));
        assertFalse(Pattern.matches(InstaYakID.pattern, "ID xyz\r\n"));
        assertFalse(Pattern.matches(InstaYakID.pattern, "ID "));
        assertTrue(Pattern.matches(InstaYakID.pattern, "ID xYz"));
        assertTrue(Pattern.matches(InstaYakID.pattern, "ID XYZ"));
    }

}