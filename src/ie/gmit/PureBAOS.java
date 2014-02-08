package ie.gmit;

import java.io.ByteArrayOutputStream;

public class PureBAOS extends ByteArrayOutputStream {
	//this class is only used to get a dynamic byte array.
	public byte[] getBuf() {
		return this.buf.clone();
	}
}
