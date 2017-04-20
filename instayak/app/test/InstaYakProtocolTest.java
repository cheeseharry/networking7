/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 3
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;
import instayak.app.InstaYakProtocol;
import instayak.app.User;

public class InstaYakProtocolTest {

	/**
     * findUser shoud find a user by id
     * @throws Exception if unexpected error occurs
     */
	@Test
    public void findUserTest() throws Exception {
    	
    	ArrayList<User> users = new ArrayList<>();
    	
        users.add(new User("user1", "password1"));
        users.add(new User("user2", "password2"));
        users.add(new User("user3", "password3"));
        
        assertEquals(InstaYakProtocol.findUser("user1", users).getPassword(), "password1");
        assertEquals(InstaYakProtocol.findUser("user4", users), null);
    	
    }

}
