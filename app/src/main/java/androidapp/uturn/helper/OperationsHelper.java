package androidapp.uturn.helper;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by Rakesh on 2/19/2016
 */
public class OperationsHelper {

    // Function that returns a random and secure salt string
    public String getSalt() {

        String strSalt;

        try {
            // Get an instance of Secure Random cryptographic class
            SecureRandom objSecureRandom = SecureRandom.getInstance("SHA1PRNG");

            byte[] arrSaltBytes = new byte[16];         // Using 16 bytes as it is the common standard on UNIX systems
            objSecureRandom.nextBytes(arrSaltBytes);    // Get a Random salt value
            strSalt = Arrays.toString(arrSaltBytes);

        } catch(NoSuchAlgorithmException ex) {
            strSalt = "";
        }

        return strSalt;
    }

    // Returns the MD5 hash value using the password and the salt value
    public String getHashedPassword(String strPassword, String strSalt) {

        String strHashedPwd;
        try {

            MessageDigest pwdDigest = MessageDigest.getInstance("MD5");

            // Use salt and password string to get the final hash bytes i.e., the digest
            pwdDigest.update(strSalt.getBytes());
            byte[] bytes = pwdDigest.digest(strPassword.getBytes());

            // The resultant digest value in decimal format is converted to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            strHashedPwd = sb.toString();
        }
        catch (NoSuchAlgorithmException ex) {
            strHashedPwd = "";
        }

        return strHashedPwd;
    }
}
