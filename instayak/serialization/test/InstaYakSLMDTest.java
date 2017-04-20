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

public class InstaYakSLMDTest {

    InstaYakSLMD iys = new InstaYakSLMD();

    private static final byte[] encoding;

    static {
        try {
            encoding = "SLMD\r\n".getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode", e);
        }
    }

    /**
     * encode should output a correctly formatted SLMD message to the
     * MessageOutput it is given
     */
    @Test
    public void testEncode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput mout = new MessageOutput(bout);
        try {
			new InstaYakSLMD().encode(mout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertArrayEquals(encoding, bout.toByteArray());
    }

    /**
     * decode should determine that the message is a SLMD message and pass
     * back a new InstaYakSLMD
     */
    @Test
    public void testDecodeMessageInput() {
        MessageInput min = new MessageInput(new ByteArrayInputStream(encoding));
        InstaYakSLMD iys;
		try {
			iys = (InstaYakSLMD) InstaYakSLMD.decode(min);
	        assertEquals("SLMD", iys.toString());
		} catch (InstaYakException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Constructor should properly parse the SLMD from a MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructor1() throws Exception {

        String input = "SLMD\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

     //   assertEquals(true, mi.hasNext());

        InstaYakSLMD iys = new InstaYakSLMD(mi);

      //  assertEquals(false, mi.hasNext());
        assertEquals("SLMD", iys.toString());

    }

    /**
     * toString should return a string in the format "SLMD"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void toStringTest() throws Exception {
        String expected = "SLMD";
        String actual = iys.toString();

        assertEquals(expected, actual);
    }

    /**
     * getOperation should return SLMD
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getOperation() throws Exception {
        assertEquals("SLMD", iys.getOperation());
    }

    /**
     * pattern should be a regex expression that only accepts messages of the
     * format "SLMD"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void regexPatternTest() throws Exception {

        // Valid patterns
        assertTrue(Pattern.matches(InstaYakSLMD.pattern, "SLMD"));

        // Invalid patterns
        assertFalse(Pattern.matches(InstaYakSLMD.pattern, "SLMD\r\n"));
        assertFalse(Pattern.matches(InstaYakSLMD.pattern, " SLMD "));
        assertFalse(Pattern.matches(InstaYakSLMD.pattern, "slmd"));

    }

}