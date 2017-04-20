/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 3
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app.test;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import instayak.app.InstaYakServer;
import instayak.app.User;

public class InstaYakServerTest {
	
	/**
	 * create file for file existence testing
	 * @throws IOException if file could not be created
	 */
	@BeforeClass
	public static void before() throws IOException {
		File file = new File("testCredentials.txt");
		file.createNewFile();
		
		BufferedWriter out = null;

		try {

			String newline = System.getProperty("line.separator");
			String users = "user1:password1" + newline + "user2:password2"
					+ newline + "user3:password3" + newline;

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("testCredentials.txt"), StandardCharsets.ISO_8859_1));
			
			out.write(users);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * remove file for file existence testing
	 */
	@AfterClass
	public static void after() {
		File fileToDelete = new File("testCredentials.txt");
		fileToDelete.delete();
	}

	/**
     * validArgs should make sure that the provided arguments are valid
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void validArgsTest() throws Exception {

        // Valid patterns
    	String[][] invalidArgs = {
    			{},
    			{ "x", "y", "z" }
    	};
    	
    	for (String[] args : invalidArgs) {
    		assertFalse(InstaYakServer.validateArgs(args));
    	}

        String[][] validArgs = {
    			{ "5000", "10", "testCredentials.txt" }
    	};
        
        for (String[] args : validArgs) {
    		assertTrue(InstaYakServer.validateArgs(args));
    	}
    }
    
    /**
     * fileExists should only return true if an imageExists
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void fileExistsTest() throws Exception {
    	
    	assertTrue(InstaYakServer.fileExists("testCredentials.txt"));
    	assertFalse(InstaYakServer.fileExists("fileThatDoesNotExist.txt"));
        
    }
    
    /**
     * getUsers should gather an arraylist of users from a credentials file
     * @throws Exception if unexpected error occurs
     */
    @Test
    public void getUserListTest() throws Exception {
    	
    	ArrayList<User> users = new ArrayList<>();
    	
        users.add(new User("user1", "password1"));
        users.add(new User("user2", "password2"));
        users.add(new User("user3", "password3"));
        
        ArrayList<User> usersList = InstaYakServer.getUserList("testCredentials.txt");
        
        for (User user : usersList) {
        	System.out.println(user.getId() + " - " + user.getPassword());
        }
        
        for (int i = 0; i < usersList.size(); i++) {
        	assertTrue(users.get(i).getId().equals(usersList.get(i).getId()));
        	assertTrue(users.get(i).getPassword().equals(usersList.get(i).getPassword()));
        }
    	
    }
    
}


























