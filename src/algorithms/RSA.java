package src.algorithms;

import java.math.BigInteger;
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
	private byte[] cipheredMsg;
	private byte[] plainMsg;
	private static RSA instance = null;
	
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
			System.out.println("Creating new instance");
			instance = new RSA();
		}
		System.out.println("Using existing instance");
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
	
	public byte[] getPlainMsg() {
		return this.plainMsg;
	}
	
	public byte[] getCipheredMsg() {
		return this.cipheredMsg;
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
			keyGen.initialize(512);
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
			System.out.println(mode.getValue());
			cipher.init(mode.getValue(), mode == Mode.ENCRYPT ? this.uk : this.rk);
			this.cipheredMsg = cipher.doFinal(text.getBytes());
			return String.format("%040x", new BigInteger(1, this.cipheredMsg));
		} catch(Exception e) {
			System.out.println(e.getMessage());
		} 
		return "";
	}
}