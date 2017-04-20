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
import java.util.Base64;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class InstaYakUOnTest {

    private static final byte[] encoding;
    private static final byte[] image = new byte[] {0x7F, (byte) 0xC8, 0x23, (byte) 0xB3, 0x5};
    private static final byte[] imageBase64;
    private static final String imageChars;
    private InstaYakUOn iyu;

    static {
        try {
            imageBase64 = Base64.getEncoder().withoutPadding().encode(image);
            imageChars = new String(imageBase64, "ISO-8859-1");
            encoding = ("UOn Movies1 " + new String(imageBase64, "ISO-8859-1") + "\r\n").getBytes("ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode", e);
        }
    }

    public InstaYakUOnTest() throws Exception {
        iyu = new InstaYakUOn("Movies1", image);
    }

    /**
     * encode should output a correctly formatted UOn message to the
     * MessageOutput it is given
     */
    @Test
    public void testEncode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        MessageOutput mout = new MessageOutput(bout);
        try {
			iyu.encode(mout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertArrayEquals(encoding, bout.toByteArray());
    }

    /**
     * decode should determine that the message is a UOn message and pass
     * back a new InstaYakUOn with the proper category and image stored
     */
    @Test
    public void testDecodeMessageInput() {
        MessageInput min = new MessageInput(new ByteArrayInputStream(encoding));
        InstaYakUOn iyu;
		try {
			iyu = (InstaYakUOn) InstaYakMessage.decode(min);
	        assertArrayEquals(image, iyu.getImage());
	        assertEquals("Movies1", iyu.getCategory());
		} catch (InstaYakException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    /**
     * Constructor should properly parse the category and image from a
     * MessageInput
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void messageInputConstructorTest1() throws Exception {
        String input = "UOn Movies1 f8gjswU\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = new MessageInput(in);

       // assertEquals(true, mi.hasNext());

        InstaYakUOn iyu = new InstaYakUOn(mi);

      //  assertEquals(false, mi.hasNext());
        assertArrayEquals(image, iyu.getImage());
        assertEquals("Movies1", iyu.getCategory());
    }

    /**
     * Constructor should store the category and image it is given if they are
     * both valid
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void categoryImageConstructorTest1() throws Exception {
        assertArrayEquals(image, iyu.getImage());
        assertEquals("Movies1", iyu.getCategory());
    }

    /**
     * setCategory should throw an InstaYakException if it is passed a null value
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setCategoryTest1() throws Exception {
        iyu.setCategory(null);
    }

    /**
     * setCategory should throw an InstaYakException if it is passed an invalid
     * category
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setCategoryTest2() throws Exception {
        // contains asterisks so is not a valid category
        iyu.setCategory("***");
    }

    /**
     * setImage should throw an InstaYakException if it is passed a null value
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void setImageTest1() throws Exception {
        iyu.setImage(null);
    }

    /**
     * toString should return a string in the format "UOn: Category=Movie Image=500 bytes"
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void toStringTest1() throws Exception {
        assertEquals("UOn: Category=Movies1 Image=5 bytes", iyu.toString());
    }

    /**
     * getOperation should return UOn
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getOperation() throws Exception {
        assertEquals("UOn", iyu.getOperation());
    }

    /**
     * encodeImage should take a byte array holding and image and convert it
     * to a base64 encoded string
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void encodeImageTest() throws Exception {
        assertEquals(imageChars, InstaYakUOn.encodeImage(image));
    }

    /**
     * encodeImage should take a byte array holding and image and convert it
     * to a base64 encoded string
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void decodeImageTest() throws Exception {
        assertArrayEquals(image, InstaYakUOn.decodeImage(imageChars));
    }

    /**
     * pattern should be a regex expression that only accepts messages of the
     * format "UOn (category) (image)" where (category) is 1+
     * alphanumeric characters and (image) is an image file encoded using "The
     * Base64 Alphabet" as specified in Table 1 of RFC 4648 and RFC 2045 with
     * no padding
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void regexPatternTest() throws Exception {

        // Valid patterns
        assertTrue(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 " + new String(imageBase64, "ISO-8859-1")));
        assertTrue(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 aA0+/"));
        assertTrue(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 a"));
        assertTrue(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 A"));
        assertTrue(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 0"));
        assertTrue(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 +"));
        assertTrue(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 /"));
        assertTrue(Pattern.matches(InstaYakUOn.pattern, "UOn movies aA0+/"));

        // Invalid patterns
        assertFalse(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 aA0+/*"));
        assertFalse(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 aA0+/="));
        assertFalse(Pattern.matches(InstaYakUOn.pattern, "UOn  Movies1 aA0+/"));
        assertFalse(Pattern.matches(InstaYakUOn.pattern, "Movies aA0+/"));
        assertFalse(Pattern.matches(InstaYakUOn.pattern, "UOn Movies1 aA0+/ a"));
        assertFalse(Pattern.matches(InstaYakUOn.pattern, "UOn Movies"));

    }

}