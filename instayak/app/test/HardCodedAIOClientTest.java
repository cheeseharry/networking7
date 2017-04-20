/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 7
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class HardCodedAIOClientTest {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket socket = new Socket("localhost", 5000);
		
		DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
	
		InputStream in = socket.getInputStream();
		
		boolean messageRead = false;
		while (!messageRead) {
			if (hasNext(in)) {
				
				byte[] buff = new byte[100];
				
				in.read(buff);
				
				System.out.println(new String(buff));
				messageRead = true;
			}
		}
		

		outToServer.writeBytes("test 1");
		
		messageRead = false;
		while (!messageRead) {
			if (hasNext(in)) {
				
				byte[] buff = new byte[100];
				
				in.read(buff);
				
				System.out.println(new String(buff));
				messageRead = true;
			}
		}
		
		outToServer.writeBytes("test 2");
		
		messageRead = false;
		while (!messageRead) {
			if (hasNext(in)) {
				
				byte[] buff = new byte[100];
				
				in.read(buff);
				
				System.out.println(new String(buff));
				messageRead = true;
			}
		}
		
		socket.close();
		
	}

	/**
	 * indicates whether or not there are any more messages
	 *
	 * @return true if there are one or more messages, false otherwise
	 */
	public static boolean hasNext(InputStream in) {
		boolean next = false;
	    try {
			next = in.available() != 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return next;
	}
	
}
