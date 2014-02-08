package ie.gmit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class FileEncryptor implements Encryptor {
	// file encryptor
	private Cipher cypher; // a cypher for encrypt and decrypt
	private static SecretKeySpec defaultKey; // if user didn't provide key i use
												// a static
												// default key, but it will
												// change when restart
												// the RMI service
	private File saveDir; // a folder to save output file

	public FileEncryptor() {
		// find relative folder tmp
		saveDir = new File("tmp/");
		// if not exist create one
		if (!saveDir.isDirectory())
			saveDir.mkdir();
		// if the defaultkey hasn't been created, create one
		if (FileEncryptor.defaultKey == null) {
			try {
				KeyGenerator keyGen = KeyGenerator.getInstance("AES");
				keyGen.init(128);
				FileEncryptor.defaultKey = (SecretKeySpec) keyGen.generateKey();
			} catch (NoSuchAlgorithmException e) {
				System.out.println(e.getMessage());
			}
		}
		// if the cypher is not available create one
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

	public String encrypt(String inputFileName, String key) throws Exception {
		SecretKeySpec tempKey = null;
		System.out.println("Encrypted File: " + inputFileName);
		FileInputStream inputStream = new FileInputStream(inputFileName);
		// create the output file after encryption add an extension .cheng
		File tmpFile = new File(inputFileName);
		String outFileName = this.saveDir.getAbsolutePath() + "/"
				+ tmpFile.getName() + ".cheng";
		FileOutputStream outputStream = new FileOutputStream(outFileName);
		// if user has put in the key, generate a key by the string that user
		// has put in
		if (key != null) {
			tempKey = this.generateKey(key);
		} else {
			// otherwise use defaultkey initialized before
			tempKey = FileEncryptor.defaultKey;
		}
		cypher.init(Cipher.ENCRYPT_MODE, tempKey);
		// read and write from inputfile to outputfile through a buffer bytes
		// this buff array could be improved by using PureBAOS class, but I
		// haven't fully
		// understand the GZIP mechanism
		// ***********************//
		// get help form Ru Peng student ID: G00307401
		// I was used PureBAOS to create dynamic buffer before,but not work
		// **********************//
		byte[] b = new byte[20480];
		while (inputStream.read(b) > 0) {
			outputStream.write(cypher.doFinal(b));
		}
		inputStream.close();
		outputStream.close();
		System.out.println("To: " + outFileName);
		return outFileName;
	}

	public String decrypt(String inputFileName, String key) throws Exception {
		if (!getExtension(inputFileName).equalsIgnoreCase("cheng")) {
			// the input file must have extension .cheng
			return "Your File has wrong extension.";
		}
		SecretKeySpec tempKey = null;
		// if user has put in the key, generate a key by the string that user
		// has put in
		if (key != null) {
			tempKey = this.generateKey(key);
		} else {
			// otherwise use defaultkey initialized before
			tempKey = FileEncryptor.defaultKey;
		}
		cypher.init(Cipher.DECRYPT_MODE, tempKey);
		FileInputStream inputStream = new FileInputStream(inputFileName);
		File tmpFile = new File(inputFileName);
		// create output file
		String outFileName = this.saveDir.getAbsolutePath() + "/"
				+ getFileName(tmpFile.getName());
		FileOutputStream outputStream = new FileOutputStream(outFileName);
		// read and write from inputfile to outputfile through a buffer bytes
		// this buff array could be improved by using PureBAOS class, but I
		// haven't fully
		// understand the GZIP mechanism
		// ***********************//
		// get help form Ru Peng student ID: G00307401
		// I was used PureBAOS to create dynamic buffer before,but not work
		// **********************//
		byte[] b = new byte[20496];
		// get dynamic buffer
		PureBAOS pb = new PureBAOS();
		byte[] temp = pb.getBuf();
		while (inputStream.read(b) > 0) {
			temp = cypher.doFinal(b);
		}
		// after read action because we defined a byte array with fixed size
		// we should delete the extra null values
		// otherwise the output file be represent wrong and take more space
		outputStream.write(trim(temp));
		pb.close();
		inputStream.close();
		outputStream.close();
		return outFileName;
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

	private String getExtension(String f) {
		// get extension of a file
		String ext = "";
		int i = f.lastIndexOf('.');

		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1);
		}
		return ext;
	}

	private String getFileName(String f) {
		// get name of a file
		String fname = "";
		int i = f.lastIndexOf('.');

		if (i > 0 && i < f.length() - 1) {
			fname = f.substring(0, i);
		}
		return fname;
	}

	private byte[] trim(byte[] in) {
		// remove the extra space of a byte array
		byte[] temp = in;
		int realLength = 0;
		for (int i = 0; i < temp.length; i++) {// count length
			if (temp[i] != 0) {
				realLength = i + 1;
			}
		}
		byte[] out = new byte[realLength];
		for (int j = 0; j < out.length; j++) {// copy data
			out[j] = temp[j];
		}
		return out;
	}
}
