/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 0
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization.test;

import org.junit.Test;
import java.io.ByteArrayOutputStream;

import instayak.serialization.*;

import static org.junit.Assert.*;

public class MessageOutputTest {

    /**
     * write message constructor should throw a NullPointerException if
     * it is given a null stream
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=NullPointerException.class)
    public void constructorTest1() throws Exception {
        @SuppressWarnings("unused")
		MessageOutput mo = new MessageOutput(null);
    }

    /**
     * writeMessage should convert the given string to an array of bytes
     * using the ISO-8859-1 character set and should add "\r\n" after each
     * message
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void writeMessageTest() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MessageOutput mo = new MessageOutput(os);

        mo.writeMessage("INSTAYAK 1.0");
        mo.writeMessage("INSTAYAK 2.0");

        String output = os.toString("ISO-8859-1");
        assertEquals(output, "INSTAYAK 1.0\r\nINSTAYAK 2.0\r\n");

    }

}