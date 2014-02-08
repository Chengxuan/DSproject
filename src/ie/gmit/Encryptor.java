package ie.gmit;

public interface Encryptor {
	public String encrypt(String input, String key) throws Exception;

	public String decrypt(String input, String key) throws Exception;
}
