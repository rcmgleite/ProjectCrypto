package src.algorithms;

import javax.xml.bind.DatatypeConverter;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class Utils {
	
	/*
	 *	Convert base64 String to bin byte[] 
	 */
	public static byte[] base64ToBin(String s) {
		if (s == null || s.trim().length() == 0) {
			return null;
		}
		return DatatypeConverter.parseBase64Binary(new String(s.getBytes()));
	}
	
	/*
	 *	Convert bin byte[] to base64 String 
	 */
	public static String binToBase64(byte[] bin) {
		return Base64.encode(bin);
	}
	
	/*
	 *	Convert hex String to bin byte[] 
	 */
	public static byte[] hexToBinary(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}
	
	/*
	 *	Convert bin byte[] to hex String 
	 */
	public static String BinaryToHex(byte[] bin) {
		return DatatypeConverter.printHexBinary(bin);
	}
	
	/*
	 *	Convert base 64 String to default encoding string
	 *	TODO - pass encoding as parameter  
	 */
	public static String base64ToString(String s) throws Exception {
		return new String(Base64.decode(s.getBytes()));
	}
	
	/*
	 *	Convert bin to default encoding String
	 *	TODO - pass encoding as parameter 
	 */
	public static String binToString(byte[] bin) throws Exception {
		return Utils.base64ToString(Utils.binToBase64(bin));
	}
}
