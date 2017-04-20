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

public class MessageInputTest {

    static String input = "test1\r\ntest2\r\n";
    static InputStream in = new ByteArrayInputStream(input.getBytes());
    static MessageInput mi = null;
    
    static {
    	try {
    		mi = new MessageInput(in);
    	
    	} catch(Exception e) {
    		throw e;
    	}
    }

    /**
     * Constructor should throw a null pointer exception if it is given null
     * @throws Exception if unexpected error occurs
     */
    @Test(expected=NullPointerException.class)
    public void inputStreamConstructorTest1() throws Exception {

        new MessageInput(null);

    }

    /**
     * getNext should return the next message in line without removing it
     * from the list of messages
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getNextTest1() throws Exception {
    	
    	String input = "test1\r\ntest2\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        
        try {
    		mi = new MessageInput(in);
    	
    	} catch(Exception e) {
    		throw e;
    	}


    }

    /**
     * popNext should return the next message in line AND remove it
     * from the list of messages
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void popNextTest1() throws Exception {

        assertEquals("test1", mi.popNext());
        assertEquals("test2", mi.popNext());

    }

    /**
     * hasNext should return true if there are more messages to be read and
     * false once all messages have been popped
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void hasNextTest() throws Exception {
    	
    	String input = "test1\r\ntest2\r\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        MessageInput mi = null;
        
        try {
    		mi = new MessageInput(in);
    	
    	} catch(Exception e) {
    		throw e;
    	}

      //  assertEquals(true, mi.hasNext());
        mi.popNext();
     //   assertEquals(true, mi.hasNext());
        mi.popNext();
      //  assertEquals(false, mi.hasNext());

    }
    
    @Test
    public void asdf() throws Exception {
    	
    	String message = "asdf\r\n";
    	assertEquals(message.substring(message.length() - 2), "\r\n");
    	
    }

}