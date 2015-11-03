package src.algorithms;

import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DSA {

	/*
	 * Create a signature based on a msg, a private key and a hash algorithm
	 */
	public static byte[] sign(String msg, String rKey, String hashAlgorithm) {
		try {
			PrivateKey privateKey = privateKeyFromHex(rKey);
			String parsedAlgorithm = getAlgorithm(hashAlgorithm);
			Signature dsa = Signature.getInstance(parsedAlgorithm);
			dsa.initSign(privateKey);

			dsa.update(msg.getBytes());
			return dsa.sign();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * Verify if signature is valid using a public key
	 */
	public static String verify(String sigData, String signature, String uKey, String hashAlgorithm) {
		PublicKey publicKey = publicKeyFromHex(uKey);
		byte[] data = Utils.hexToBinary(signature);

		try {
			String parsedAlgorithm = getAlgorithm(hashAlgorithm);
			Signature sig = Signature.getInstance(parsedAlgorithm);
			sig.initVerify(publicKey);
			sig.update(sigData.getBytes());
			return sig.verify(data) == true ? "SUCCESS - Signature is Valid!" : "FAILURE - Signature is not valid";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}
	}

	public static PublicKey publicKeyFromHex(String hex) {
		try {

			byte[] data = Utils.hexToBinary(hex);
			KeySpec publicKeySpec = new X509EncodedKeySpec(data);
			KeyFactory keyfactory = KeyFactory.getInstance("DSA");
			return keyfactory.generatePublic(publicKeySpec);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static PrivateKey privateKeyFromHex(String hex) {
		try {
			byte[] data = Utils.hexToBinary(hex);
			KeySpec privateKeySpec = new PKCS8EncodedKeySpec(data);
			KeyFactory keyfactory = KeyFactory.getInstance("DSA");
			return keyfactory.generatePrivate(privateKeySpec);
		} catch (Exception e) {
			System.out.println("privateKeyFromHex: " + e.getMessage());
			return null;
		}
	}

	public static byte[] sha1(String msg) {
		return sha(msg, "SHA-1");
	}

	public static byte[] sha256(String msg) {
		return sha(msg, "SHA-256");
	}

	private static byte[] sha(String msg, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(msg.getBytes());
			return md.digest();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/*
	 * Get algorithm name from hash algorithm as parameter
	 */
	private static String getAlgorithm(String hashAlgorithm) {
		return hashAlgorithm.replace("-", "") + "withDSA";
	}

	/*
	 * Method created to generate valid dsa keys for tests..
	 */
	@SuppressWarnings(value = "all")
	private static void generateKeys() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
			keyPairGenerator.initialize(1024);
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			DSAPrivateKey privateKey = (DSAPrivateKey) keyPair.getPrivate();
			DSAPublicKey publicKey = (DSAPublicKey) keyPair.getPublic();

			FileOutputStream keyfos = new FileOutputStream("generated_DES_rk");
			System.out.println(privateKey.getFormat());
			keyfos.write(Utils.BinaryToHex(privateKey.getEncoded()).getBytes());
			keyfos.close();

			FileOutputStream keyfos2 = new FileOutputStream("generated_DES_uk");
			System.out.println(publicKey.getFormat());
			keyfos2.write(Utils.BinaryToHex(publicKey.getEncoded()).getBytes());
			keyfos2.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
