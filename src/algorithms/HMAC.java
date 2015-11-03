package src.algorithms;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {

	/*
	 * compute calculates de hmac of a msg using a specific key and a hash
	 * algorithm Return a String encoded in HEX format
	 */
	public static byte[] compute(String hashAlgorithm, String msg, String key)
			throws NoSuchAlgorithmException, InvalidKeyException {
		String parsedAlgorithm = "Hmac" + hashAlgorithm.replace("-", "");
		Mac hmac = Mac.getInstance(parsedAlgorithm);
		SecretKeySpec sKey = new SecretKeySpec(key.getBytes(), parsedAlgorithm);
		hmac.init(sKey);
		return hmac.doFinal(msg.getBytes());
	}
}
