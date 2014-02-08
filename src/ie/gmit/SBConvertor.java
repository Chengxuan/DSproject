package ie.gmit;

public class SBConvertor {
	// this class is used to convert a hex string to byte array and vice versa
	public static String byteToString(byte[] byteArray) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			String hex = Integer.toHexString(byteArray[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}

		return sb.toString();
	}

	public static byte[] stringToByte(String hexString) {
		byte[] byteArray = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length() / 2; i++) {
			int high = Integer.parseInt(hexString.substring(i * 2, i * 2 + 1),
					16);
			int low = Integer.parseInt(
					hexString.substring(i * 2 + 1, i * 2 + 2), 16);
			byteArray[i] = (byte) (high * 16 + low);
		}
		return byteArray;
	}
}
