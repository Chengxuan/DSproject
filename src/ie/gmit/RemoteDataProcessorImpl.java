package ie.gmit;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteDataProcessorImpl extends UnicastRemoteObject implements
		RemoteDataProcessor {
	// implementation of Remotedataprocessor

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataProcessor dp;

	protected RemoteDataProcessorImpl(int option) throws RemoteException {
		super();
		// initialise DataProcessor as a string or file processor
		// 0 for string
		// 1 for file
		dp = new DataProcessor(option);

	}

	// Bellows are delegate methods
	/**
	 * @param input
	 * @return
	 * @see ie.gmit.DataProcessor#compress(java.lang.String)
	 */
	public String compress(String input) throws Exception {
		return dp.compress(input);
	}

	/**
	 * @param input
	 * @return
	 * @see ie.gmit.DataProcessor#decompress(java.lang.String)
	 */
	public String decompress(String input) throws Exception {
		return dp.decompress(input);
	}

	/**
	 * @param input
	 * @param key
	 * @return
	 * @see ie.gmit.DataProcessor#encrypt(java.lang.String, java.lang.String)
	 */
	public String encrypt(String input, String key) throws Exception {
		return dp.encrypt(input, key);
	}

	/**
	 * @param input
	 * @param key
	 * @return
	 * @see ie.gmit.DataProcessor#decrypt(java.lang.String, java.lang.String)
	 */
	public String decrypt(String input, String key) throws Exception {
		return dp.decrypt(input, key);
	}

}
