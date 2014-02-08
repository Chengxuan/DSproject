package ie.gmit;

import java.io.Serializable;
import java.rmi.Remote;

public interface RemoteDataProcessor extends Remote, Serializable {
	// remote data processor
	// this is a remote object will passed by reference to the
	// client of RMI servcie
	public String encrypt(String input, String key) throws Exception;

	public String decrypt(String input, String key) throws Exception;

	public String compress(String input) throws Exception;

	public String decompress(String input) throws Exception;
}
