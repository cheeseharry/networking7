/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 2
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.serialization;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputeHash {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String nonce = "12345";
        String password = "password";
        System.out.println(computeHash(nonce + password));
    }

    public static String computeHash(String msg)
            throws UnsupportedEncodingException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buf = md5.digest(msg.getBytes("ISO8859_1"));
            return hashToString(buf);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to get MD5", e);
        }
    }
    
    public static String hashToString(byte[] bytes) {
        String hexHash = "";
        for (byte b : bytes) {
            String v = Integer.toHexString(Integer.valueOf(b & 0xff));
            if (v.length() == 1)
                v = "0" + v;
            hexHash += v.toUpperCase();
        }

        return hexHash;
    }
}
