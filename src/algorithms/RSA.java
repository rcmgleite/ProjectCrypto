package src.algorithms;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/*
 * 	In RSA, the encryption uses the PUBLIC KEY and de decryption uses the PRIVATE LEY
 */
public class RSA {
	private PublicKey uk;
	private PrivateKey rk;
	private static RSA instance = null;
	private static final int KEY_SIZE = 1024;
	
	/*
	 *	Enum from cipher mode - its just a wrapper for Cipher.ENCRYPT_MODE and Cipher_DECRYPT_MODE 
	 */
	public enum Mode {
		ENCRYPT(Cipher.ENCRYPT_MODE),
		DECRYPT(Cipher.DECRYPT_MODE);
		
		private final int value;
		
		private Mode(int v) {
			this.value = v;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	/*
	 *	Singleton access method 
	 */
	public static RSA getInstance() {
		if (instance == null) {
			instance = new RSA();
		}
		return instance;
	}
	
	/*
	 *	Getters 
	 */
	public PrivateKey getPrivateKey() {
		return this.rk;
	}
	
	public PublicKey getPublicKey() {
		return this.uk;
	}
	
	/*
	 *	Private RSA constructor for singleton pattern 
	 */
	private RSA() {
		this.uk = null;
		this.rk = null;
	}
	
	/*
	 *	generates the keys for RSA algorithm 
	 */
	public void generateKeyPair() {
		try{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(KEY_SIZE);
			KeyPair kp = keyGen.generateKeyPair();
			rk = kp.getPrivate();
			uk = kp.getPublic();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 *	 Encrypt a plain text using RSA algorithm
	 */
	public String encrypt(String plainText) {
		return cipherExecute(plainText, Mode.ENCRYPT);
	}
	
	/*
	 *	Decrypt a encrypted text using RSA algorithm
	 */
	public String decrypt(String cipherText) {
		return cipherExecute(cipherText, Mode.DECRYPT);
	}
	
	/*
	 *	Method that really executes the encrypt or decrypt operation based on the mode argument 
	 */
	private String cipherExecute(String text, Mode mode) {
		try{
			Cipher cipher = Cipher.getInstance("RSA");
			byte[] ciphered;
			String toReturn;
			
			if(mode == Mode.ENCRYPT) {
				cipher.init(Cipher.ENCRYPT_MODE, this.uk);
				ciphered = cipher.doFinal(text.getBytes());
				toReturn = Utils.binToBase64(ciphered);
			} else {
				cipher.init(Cipher.DECRYPT_MODE, this.rk);
				ciphered = cipher.doFinal(Utils.base64ToBin(text));
				toReturn = "Decoded cipher: '" + Utils.binToString(ciphered) + "'";
			}
			
			return toReturn;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return "[ERROR] " + e.getMessage();
		} 
	}
}