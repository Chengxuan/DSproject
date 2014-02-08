package ie.gmit;

public interface Compressor {
	// interface of a compressor
	// I define two kinds of compressor : string and file compressors
	public String compress(String input) throws Exception;

	public String decompress(String input) throws Exception;

}
