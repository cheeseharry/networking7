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

public class InstaYakVersionTest {

    InstaYakVersion iyv = new InstaYakVersion();

    private static final byte[] encoding;

    static {
        try {
            encoding = "INSTAYAK 1.0\r\n".getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode", e);
        }
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
			new InstaYakVersion().encode(mout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertArrayEquals(encoding, bout.toByteArray());
    }

    /**
     * decode should determine that the message is a version message and pass
     * back a new InstaYakVersion with the proper version stored
     */
    @Test
    public void testDecodeMessageInput() {
        MessageInput min = new MessageInput(new ByteArrayInputStream(encoding));
        InstaYakVersion iyv;
		try {
			iyv = (InstaYakVersion) InstaYakMessage.decode(min);
	        assertEquals("1.0", iyv.getVersion());
		} catch (InstaYakException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor should properly parse the version number from a MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructor1() throws Exception {

        String input = "INSTAYAK 2.0\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

     //   assertEquals(true, mi.hasNext());

        InstaYakVersion iyv = new InstaYakVersion(mi);

       // assertEquals(false, mi.hasNext());
        assertEquals("2.0", iyv.getVersion());

    }

    /**
     * Constructor should properly parse a longer version number from a
     * MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructor2() throws Exception {

        String input = "INSTAYAK 12.10\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

        //assertEquals(true, mi.hasNext());

        InstaYakVersion iyv = new InstaYakVersion(mi);

        //assertEquals(false, mi.hasNext());
        assertEquals("12.10", iyv.getVersion());

    }

    /**
     * toString should return a string in the format "InstaYak"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void toStringTest() throws Exception {
        String expected = "InstaYak";
        String actual = iyv.toString();

        assertEquals(expected, actual);
    }

    /**
     * getOperation should return INSTAYAK
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getOperation() throws Exception {
        assertEquals("INSTAYAK", iyv.getOperation());
    }

    /**
     * setVersion should update the version
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void setVersion1() throws Exception {
        assertEquals("1.0", iyv.getVersion());
        iyv.setVersion("2.0");
        assertEquals("2.0", iyv.getVersion());
    }

    /**
     * setVersion should throw an InstaYakException if version value is not
     * in the valid format
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setVersion2() throws Exception {
        iyv.setVersion("x");
    }

    /**
     * setVersion should throw a NullPointerException if given a null value
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=NullPointerException.class)
    public void setVersion3() throws Exception {
        iyv.setVersion(null);
    }

    /**
     * pattern should be a regex expression that only accepts messages of the
     * format "INSTAYAK (version)" where (version) is one or more
     * numeric characters, followed by a period, followed by one of more
     * numeric characters
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void regexPattern1() throws Exception {
        assertTrue(Pattern.matches(InstaYakVersion.pattern, "INSTAYAK 1.0"));
        assertFalse(Pattern.matches(InstaYakVersion.pattern, "INSTAYA 1.0"));
        assertFalse(Pattern.matches(InstaYakVersion.pattern, "INSTAYAK 1.0\r\n"));
        assertFalse(Pattern.matches(InstaYakVersion.pattern, "INSTAYAK"));
        assertTrue(Pattern.matches(InstaYakVersion.pattern, "INSTAYAK 2.0"));
        assertTrue(Pattern.matches(InstaYakVersion.pattern, "INSTAYAK 21234.12341"));
        assertTrue(Pattern.matches(InstaYakVersion.pattern, "INSTAYAK 21234.12341"));
    }

}