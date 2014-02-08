package ie.gmit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringCompressor implements Compressor {
	// string compressor
	@Override
	public String compress(String input) throws Exception {
		String output = "";
		if (input.length() < 1) {// the string is empty
			return input;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(baos);
		gzip.write(input.getBytes());
		gzip.flush();
		gzip.close();
		baos.flush();
		baos.close();
		// return bytes as a hex string
		output = SBConvertor.byteToString(baos.toByteArray());
		return output;
	}

	@Override
	public String decompress(String input) throws Exception {
		if (input.length() < 1) {// input string is empty
			return input;
		}
		// get a dynamic buffer
		byte[] bytes = SBConvertor.stringToByte(input);
		PureBAOS pb = new PureBAOS();
		byte[] output = pb.getBuf();
		GZIPInputStream gis;
		gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
		gis.read(output);
		gis.close();
		pb.close();
		// return string convert from the byte array
		return new String(output);
	}

}
