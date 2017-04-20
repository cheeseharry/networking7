/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 7
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app;

/**
 * A handler for various asynchronous I/O operations
 * @version 0.1
 */
public interface AIOHandler {
	/**
	 * Handle accept of new connection and return message to write.
	 * By default this is a no-op that writes nothing.
	 * 
	 * @return message to write (null if none)
	 */
	default byte[] handleAccept() {
		return null;
	}

	/**
	 * Handle write from given buffer.
	 * By default, this is a no-op.
	 * 
	 * @param writeBuff written bytes
	 */
	default void handleWrite(byte[] writeBuff) {
	}

	/**
	 * Handle read to given buffer and return any message in
	 * response to read bytes.
	 * 
	 * @param readBuff read bytes
	 * @return message in response to read bytes (null if none)
	 */
	byte[] handleRead(byte[] readBuff);
}

