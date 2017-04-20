/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 2
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app.test;

import static org.junit.Assert.*;
import org.junit.Test;
import instayak.app.InstaYakClient;

/**
 * tests the functions of the InstaYakClient
 * 
 * @author Kevin Aud
 * @version 1.0
 */
public class InstaYakClientTest {

	/**
     * validArgs should make sure that the provided arguments are valid
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void validArgsTest() throws Exception {

        // Valid patterns
    	String[][] invalidArgs = {
    			{},
    			{ "wind.ecs.baylor.edu" },
    			{ "wind.ecs.baylor.edu", "5000" },
    			{ "wind.ecs.baylor.edu", "5000", "aud" },
    			{ "wind.ecs.baylor.edu", "abc", "aud", "123" }
    	};
    	
    	for (String[] args : invalidArgs) {
    		assertFalse(InstaYakClient.validArgs(args));
    	}

        String[][] validArgs = {
    			{ "wind.ecs.baylor.edu", "5000", "aud", "123" }
    	};
        
        for (String[] args : validArgs) {
    		assertTrue(InstaYakClient.validArgs(args));
    	}
    }

}
