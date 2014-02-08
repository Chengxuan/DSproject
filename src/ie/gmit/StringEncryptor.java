package ie.gmit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class StringEncryptor implements Encryptor {
	// string encryptor

	private Cipher cypher; // cypher
	private static SecretKeySpec defaultKey;// a static default key
											// restart Server will reset it to
											// antoher value

	public StringEncryptor() {
		// if there is not default key create one
		if (StringEncryptor.defaultKey == null) {
			try {
				KeyGenerator keyGen = KeyGenerator.getInstance("AES");
				keyGen.init(128);
				StringEncryptor.defaultKey = (SecretKeySpec) keyGen
						.generateKey();
			} catch (NoSuchAlgorithmException e) {
				System.out.println(e.getMessage());
			}
		}
		// if there is no cypher create one
		if (cypher == null) {
			try {
				cypher = Cipher.getInstance("AES");
			} catch (NoSuchAlgorithmException e) {
				System.out.println(e.getMessage());
			} catch (NoSuchPaddingException e) {
				System.out.println(e.getMessage());
			}
		}

	}

	@Override
	public String encrypt(String input, String key) throws Exception {
		String output = "";
		SecretKeySpec tempKey = null;
		if (input.length() < 1) {// string is empty
			return null;
		}
		// if user has put in the key, generate a key by the string that user
		// has put in
		if (key != null) {
			tempKey = this.generateKey(key);
		} else {
			// otherwise use default key
			tempKey = StringEncryptor.defaultKey;
		}
		cypher.init(Cipher.ENCRYPT_MODE, tempKey);
		byte[] bytes = input.getBytes("UTF-8");
		byte[] bResult = cypher.doFinal(bytes);
		// convert byte array to string
		output = SBConvertor.byteToString(bResult);
		return output;
	}

	@Override
	public String decrypt(String input, String key) throws Exception {
		String output = "";
		SecretKeySpec tempKey = null;
		if (input.length() < 1) {
			return null;
		}
		// if user has put in the key, generate a key by the string that user
		// has put in
		if (key != null) {
			tempKey = this.generateKey(key);
		} else {
			// otherwise use default key
			tempKey = defaultKey;
		}
		cypher.init(Cipher.DECRYPT_MODE, tempKey);
		byte[] bytes = SBConvertor.stringToByte(input);
		byte[] bResult = cypher.doFinal(bytes);
		// create string from byte array
		output = new String(bResult);
		return output;
	}

	private SecretKeySpec generateKey(String password) throws Exception {
		// ******************//
		// get help from Adeel Gilani student ID:G00279198
		// I was using a String to Key method before, it's not work on Unix
		// system
		// I post the problem on Moodle
		// *****************//
		// --------The old method---------//
		// |KeyGenerator kgen = KeyGenerator.getInstance("AES");
		// |kgen.init(128, new SecureRandom(key.getBytes()));
		// |SecretKey secretKey = kgen.generateKey();
		// |byte[] enCodeFormat = secretKey.getEncoded();
		// |tempKey = new SecretKeySpec(enCodeFormat, "AES");
		// --------The old method---------//
		byte[] key = (password).getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		return secretKeySpec;
	}
}
