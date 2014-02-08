package ie.gmit;

import java.io.Serializable;

public class DataProcessor implements Serializable {
	// this class is a combination of compressor and encryptor
	// it can be initialised as file or string processor by the parameters
	// passed into the constructor
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Compressor cp;
	private Encryptor ep;

	public DataProcessor(int option) {
		// polymorphism
		switch (option) {
		case 0:
			// initialize as a string processor
			cp = new StringCompressor();
			ep = new StringEncryptor();
			break;
		case 1:
			// initialze as a file processor
			cp = new FileCompressor();
			ep = new FileEncryptor();
			break;
		}

	}

	// Bellows are all delegate methods from compressor and encryptor

	/**
	 * @param input
	 * @return
	 * @see ie.gmit.Compressor#compress(String)
	 */
	public String compress(String input) throws Exception {
		return cp.compress(input);
	}

	/**
	 * @param input
	 * @return
	 * @see ie.gmit.Compressor#decompress(String)
	 */
	public String decompress(String input) throws Exception {
		return cp.decompress(input);
	}

	/**
	 * @param input
	 * @param key
	 * @return
	 * @see ie.gmit.Encryptor#encrypt(byte[], byte[])
	 */
	public String encrypt(String input, String key) throws Exception {
		return ep.encrypt(input, key);
	}

	/**
	 * @param input
	 * @param key
	 * @return
	 * @see ie.gmit.Encryptor#decrypt(byte[], byte[])
	 */
	public String decrypt(String input, String key) throws Exception {
		return ep.decrypt(input, key);
	}

}
