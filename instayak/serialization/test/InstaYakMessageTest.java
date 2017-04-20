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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class InstaYakMessageTest {

    /**
     * decode should create a new InstaYakVersion if it is given a message in
     * the format "INSTAYAK (version)" where (version) is one or more
     * numeric characters, followed by a period, followed by one of more
     * numeric characters
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void decodeVersionMessage() throws Exception {

        String versionMessage = "INSTAYAK 1.0\r\n";

        InputStream in = new ByteArrayInputStream(
                versionMessage.getBytes("ISO-8859-1"));

        MessageInput mi = new MessageInput(in);

        InstaYakMessage m = InstaYakMessage.decode(mi);

        assertTrue(m instanceof InstaYakVersion);

    }

    /**
     * decode should create a new InstaYakID if it is given a message in
     * the format "ID(sp)(ID)" where (ID) is one or more alphanumeric characters
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void decodeIDMessage() throws Exception {

        String IDMessage = "ID someUserId\r\n";

        InputStream in = new ByteArrayInputStream(
                IDMessage.getBytes("ISO-8859-1"));

        MessageInput mi = new MessageInput(in);

        InstaYakMessage m = InstaYakMessage.decode(mi);

        assertTrue(m instanceof InstaYakID);

    }

    /**
     * decode should create a new InstaYakChallenge if it is given a message in
     * the format "CLNG(sp)(servnonce)" where (servnonce) is one or more
     * numeric characters
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void decodeChallengeMessage() throws Exception {

        String challengeMessage = "CLNG 1234\r\n";

        InputStream in = new ByteArrayInputStream(
                challengeMessage.getBytes("ISO-8859-1"));

        MessageInput mi = new MessageInput(in);

        InstaYakMessage m = InstaYakMessage.decode(mi);

        assertTrue(m instanceof InstaYakChallenge);

    }

    /**
     * decode should throw an InstaYakException if it is given a message that
     * does match any of the InstaYak message formats
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=InstaYakException.class)
    public void decodeInvalidTest() throws Exception {

        String invalidMessage = "invalid message";

        InputStream in = new ByteArrayInputStream(
                invalidMessage.getBytes("ISO-8859-1"));

        MessageInput mi = new MessageInput(in);

        @SuppressWarnings("unused")
		InstaYakMessage m = InstaYakMessage.decode(mi);

    }

}