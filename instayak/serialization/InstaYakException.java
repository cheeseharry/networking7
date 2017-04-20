/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 0
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization;

import java.io.Serializable;

/**
 * Exception for InstaYak handling
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakException extends Exception implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs InstaYak exception
     *
     * @param message exception message
     * @param cause exception cause
     */
    public InstaYakException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs InstaYak exception
     *
     * @param message exception message
     */
    public InstaYakException(String message) {
        super(message);
    }

}
