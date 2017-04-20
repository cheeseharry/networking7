/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 1
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Represents a InstaYak UOn message and provides serialization/deserialization.
 * The image is set/stored as raw (that is from the original file) bytes. You do
 * not need to validate the raw bytes as a valid image. The image is only
 * encoded in Base64 when serialized (that is right before decode and right
 * after encode). Java provides Base64 encoding (@see java.util.Base64). Note
 * well that you must use the "Basic" coders. Your encoding should NOT including
 * padding.
 *
 * @version 1.0
 * @author Kevin Aud
 */
public class InstaYakUOn extends InstaYakMessage {

	/**
	 * unique identifier of this operation
	 */
    public final static String operation = "UOn";

    /**
     * regex expression that tests if a string matches the format of an
     * InstaYakUOn message
     */
    public static String pattern = "^UOn [a-zA-Z0-9]+ [a-zA-Z0-9+/]+$";

    /**
     * regex expression that tests if a string matches the format of a
     * category
     */
    public static String categoryPattern = "^[a-zA-Z0-9]+$";

    /**
     * stores the category once it is extracted from a message or passed into
     * the constructor
     */
    private String category;

    /**
     * stores the image once it is extracted from a message or passed into the
     * constructor
     */
    private byte[] image;

    /**
     * Constructs UOn message using set values
     *
     * @param category UOn category
     * @param image UOn image
     * @throws InstaYakException if validation fails
     */
    public InstaYakUOn(String category, byte[] image) throws InstaYakException {
        setCategory(category);
        setImage(image);
    }

    /**
     * Constructs new UOn message using deserialization. Only parses material
     * specific to this message (that is not operation)
     *
     * @param in deserialization input source
     * @throws IOException if there are no messages to be read
     * @throws InstaYakException if message is not in correct format
     */
    public InstaYakUOn(MessageInput in) throws InstaYakException, IOException {

        // check if in has a message to be read
    	if (in.getLast() == null) {
        	if (!in.hasNext()) {
        		throw new IOException();
        	}
        	in.popNext();
        }

        // check if next message is a valid InstaYakChallenge message
        if (!Pattern.matches(pattern, in.getLast())) {
            throw new InstaYakException("Incorrect message format");
        }

        // read next message and remove it from message list
        String message = in.popLast();

        String[] messageSections = message.split(" ");

        // extract category and store it
        setCategory(messageSections[1]);

        // convert image encoding to raw image data
        byte[] rawImage = decodeImage(messageSections[2]);
        setImage(rawImage);

    }
    
    public static boolean isValidCategory(String category) {
    	return Pattern.matches("[a-zA-Z0-9]+", category);
    }

    /**
     * Returns category
     *
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets category
     *
     * @param category new category
     * @throws InstaYakException if null or invalid category
     */
    public void setCategory(String category) throws InstaYakException {
        if (category == null) {
            throw new InstaYakException("null category");
        }

        // make sure category is one or more alphanumeric characters
        if(!Pattern.matches(categoryPattern, category)) {
            throw new InstaYakException("Invalid category");
        }

        this.category = category;

    }

    /**
     * Returns image
     *
     * @return image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Sets image
     *
     * @param image new image
     * @throws InstaYakException if null image
     */
    public final void setImage(byte[] image) throws InstaYakException {

        if (image == null) {
            throw new InstaYakException("null image");
        }

        this.image = image;
    }

    /**
     * Returns a String representation ("UOn: Category=Movie Image=500 bytes")
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "UOn: Category=" + getCategory() + " Image=" + getImage().length + " bytes";
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @SuppressWarnings("static-access")
	@Override
    public String getOperation() {
        return this.operation;
    }

    /**
     * Serializes message to given output sink
     *
     * @param out serialization output sink
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        out.writeMessage(getOperation() + " " + getCategory() + " " + encodeImage(getImage()));
    }

    /**
     * converts image data to a base64 encoded string
     *
     * @param image byte array holding a jpeg image
     * @return base64 encoded string representation of an image
     */
    public static String encodeImage(byte[] image) {

        byte[] imageBase64 = Base64.getEncoder().withoutPadding().encode(image);
        return new String(imageBase64, StandardCharsets.ISO_8859_1);
    }

    /**
     * converts a base64 encoded string to raw image bytes
     *
     * @param base64Image base64 encoded string representation of an image
     * @return raw image bytes
     */
    public static byte[] decodeImage(String base64Image) {
        return Base64.getDecoder().decode(base64Image);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + Arrays.hashCode(image);
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("static-access")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstaYakUOn other = (InstaYakUOn) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (!Arrays.equals(image, other.image))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		return true;
	}

    
}
