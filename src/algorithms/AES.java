package src.algorithms;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

public class AES {

	/*
	 * Cipher mode
	 */
	public enum Mode {
		ECB, CBC, CTR
	}

	private static final int KEY_SIZE = 128;

	/*
	 * Number of columns(32-bit words) comprising the State. 4 is used for the
	 * FIPS 197 standard
	 */
	private static int nb = 4;

	/*
	 * Number of 32 bit words comprising the cipher key
	 */
	private static int nk;

	/*
	 * Number of Rounds - function of nk and nb
	 */
	private static int nr;

	/*
	 * Key Schedule
	 */
	private static byte[][] w;

	/*
	 * generate iv
	 */
	public static byte[] generateIv() {
		SecureRandom random = new SecureRandom();
		byte iv[] = new byte[16];// generate random 16 byte IV AES is always
									// 16bytes
		random.nextBytes(iv);
		return iv;
	}

	public static byte[] generateKey() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(KEY_SIZE);
			return keygen.generateKey().getEncoded();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static byte[] generatePadding(byte[] input) {
		int paddingLength = 16 - (input.length % 16);
		byte[] padding = new byte[paddingLength];
		padding[0] = (byte) 0x80;

		for (int i = 0; i < paddingLength; i++) {
			padding[i] = 0;
		}
		return padding;
	}

	/*
	 * encrypt method - currently using ONLY ECB mode by default
	 */
	public static byte[] encrypt(byte[] input, byte[] key, byte[] iv, Mode mode) {
		nk = key.length / 4;
		nr = nk + 6;
		w = keyExpansion(key);
		
		byte[] padding = generatePadding(input);
		byte[] cipheredText = new byte[input.length + padding.length];
		// AES block size = 128 bits === 16 bytes
		byte[] block = new byte[16];

		switch (mode) {
		case ECB:
			for(int i = 0; i < input.length + padding.length; i = i + 16) {
				if(input.length - i >= 16) {
					System.arraycopy(input, i, block, 0, block.length);
				} else {
					System.arraycopy(input, i, block, 0, (input.length - i) % 16);
					System.arraycopy(padding, 0, block, (input.length - i) % 16, padding.length);
				}

				block = encryptBlock(block);
				System.arraycopy(block, 0, cipheredText, i, block.length);
			}
			break;
		case CBC:
			byte[] lastCipheredBlock = iv;
			for(int i = 0; i < input.length + padding.length; i = i + 16) {
				if(input.length - i >= 16) {
					System.arraycopy(input, i, block, 0, block.length);
				} else {
					System.arraycopy(input, i, block, 0, (input.length - i) % 16);
					System.arraycopy(padding, 0, block, (input.length - i) % 16, padding.length);
				}

				block = encryptBlock(xor(lastCipheredBlock, block));
				lastCipheredBlock = block;
				System.arraycopy(block, 0, cipheredText, i, block.length);
			}
			break;
		case CTR:
			byte[] counter = iv;
			
			for(int i = 0; i < input.length + padding.length; i = i + 16) {
				if(input.length - i >= 16) {
					System.arraycopy(input, i, block, 0, block.length);
				} else {
					System.arraycopy(input, i, block, 0, (input.length - i) % 16);
					System.arraycopy(padding, 0, block, (input.length - i) % 16, padding.length);
				}

				block = xor(block, encryptBlock(counter));
				counter = incrCounter(counter);
				System.arraycopy(block, 0, cipheredText, i, block.length);
			}
			break;
		}
		
		return cipheredText;
	}
	
	/*
	 *	Increment the given input by 1 
	 */
	private static byte[] incrCounter(byte[] in) {
		BigInteger bIntValue = new BigInteger(in);
		bIntValue = bIntValue.add(BigInteger.ONE);
		in = bIntValue.toByteArray();
		return in;
	}

	/*
	 * Source: figure 5 FIPS 197 The byte[]w contains the key schedule
	 * 	Will encypt the block passed as argument 
	 */
	private static byte[] encryptBlock(byte[] block) {
		byte[][] state = new byte[4][nb];

		state = unidimensional2bidimensional(block);
		printState(state);
		
		state = addRoundKey(state, w, 0);
		printState(state);

		// first rounds include mixColumns() step
		for (int r = 1; r < nr; r++) {
			System.out.println("[DEBUG] round = " + r);
			state = subBytes(state);
			printState(state);
			state = shiftRows(state);
			printState(state);
			state = mixCloumns(state);
			printState(state);
			state = addRoundKey(state, w, r);
			printState(state);
		}

		// final round doesn't include mixColumns() step
		state = subBytes(state);
		printState(state);
		state = shiftRows(state);
		printState(state);
		state = addRoundKey(state, w, nr);
		printState(state);

		// copy state to toReturn
		return bidimensional2unidimensional(state);
	}

	private static void printState(byte[][] state) {
		for(int i = 0; i < state.length; i++) {
			System.out.println(Utils.BinaryToHex(state[i]));
		}
	}
	
	/*
	 * decrypt
	 */
	public static byte[] decrypt(byte[] input, byte[] key, byte[] iv, Mode mode) {
		nk = key.length / 4;
		nr = nk + 6;
		w = keyExpansion(key);
		
		// AES block size = 128 bits === 16 bytes
		byte[] block = new byte[16];
		byte[] plainText = new byte[input.length];
		switch (mode) {
		case ECB:
			for(int i = 0; i < input.length; i = i + 16) {
				System.arraycopy(input, i, block, 0, block.length);
				block = decryptBlock(block);
				System.arraycopy(block, 0, plainText, i, block.length);
			}
			break;
		case CBC:
			byte[] lastCipheredBlock = iv;
			for(int i = 0; i < input.length; i = i + 16) {
				System.arraycopy(input, i, block, 0, block.length);
				block = xor(lastCipheredBlock, decryptBlock(block));
				lastCipheredBlock = block;
				System.arraycopy(block, 0, plainText, i, block.length);
			}
			break;
		case CTR:
			byte[] counter = iv;
			for(int i = 0; i < input.length; i = i + 16) {
				System.arraycopy(input, i, block, 0, block.length);
				block = xor(block, encryptBlock(counter));
				counter = incrCounter(counter);
				System.arraycopy(block, 0, plainText, i, block.length);
			}
			break;
		}

		return deletePadding(plainText);
	}

	/*
	 * Remove the zeros used for padding
	 */
	private static byte[] deletePadding(byte[] input) {
		int count = 0;

		int i = input.length - 1;
		while (input[i] == 0) {
			count++;
			i--;
		}

		byte[] toReturn = new byte[input.length - count];
		System.arraycopy(input, 0, toReturn, 0, toReturn.length);
		return toReturn;
	}

	/*
	 * Source: Figure 12 - FIPS 197
	 */
	private static byte[] decryptBlock(byte[] input) {
		byte[][] state = new byte[4][nb];

		state = unidimensional2bidimensional(input);

		state = addRoundKey(state, w, nr);
		for (int round = nr - 1; round >= 1; round--) {
			state = invSubBytes(state);
			state = invShiftRows(state);
			state = addRoundKey(state, w, round);
			state = invMixColumns(state);

		}
		state = invSubBytes(state);
		state = invShiftRows(state);
		state = addRoundKey(state, w, 0);

		return bidimensional2unidimensional(state);
	}

	/*
	 * performs a cyclic permutation Input: [a0, a1, a2, a3] Output: [a1, a2,
	 * a3, a0]
	 */
	private static byte[] rotWord(byte[] w) throws Exception {
		if (w.length != 4) {
			throw new Exception(notFourByteWordExceptionMsg);
		}
		byte[] toReturn = new byte[4];
		toReturn[0] = w[1];
		toReturn[1] = w[2];
		toReturn[2] = w[3];
		toReturn[3] = w[0];

		return toReturn;
	}

	/*
	 * function that take a 4 byte input and applies the S-box to each of the
	 * four bytes // FIXME - peguei pronta essa função.. não parece muito
	 * correta
	 */
	private static byte[] subWord(byte[] input) throws Exception {
		if (input.length != 4) {
			throw new Exception(notFourByteWordExceptionMsg);
		}
		byte[] toReturn = new byte[4];

		for (int i = 0; i < 4; i++) {
			// 0x000000ff = first 24 bits = 0 and last 8 1.
			toReturn[i] = (byte) (sbox[input[i] & 0x000000ff] & 0xff);
		}

		return toReturn;
	}

	/*
	 * non-linear byte substitution using s-box // FIXME - mesmo problema da
	 * subWord
	 */
	private static byte[][] subBytes(byte[][] state) {
		byte[][] toReturn = new byte[4][nb];
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < nb; col++) {
				toReturn[row][col] = (byte) (sbox[(state[row][col] & 0x000000ff)] & 0xff);
			}
		}
		return toReturn;
	}

	/*
	 * Returns a linear array of 4-byte words with size nr+1 called key schedule
	 * nr = number of rounds
	 *
	 * Algorithm was implemented based on figure 11 of FISP 197
	 */
	private static byte[][] keyExpansion(byte[] key) {
		byte[][] toReturn = new byte[nb * (nr + 1)][nb];
		int i = 0;

		while (i < nk) {
			for (int j = 0; j < nb; j++) {
				toReturn[i][j] = key[i * nb + j];
			}

			i++;
		}

		i = nk;

		while (i < (nb * (nr + 1))) {
			byte[] tmp = new byte[4]; // temporary word
			for (int j = 0; j < 4; j++) {
				tmp[j] = toReturn[i - 1][j];
			}
			try {
				if (i % nk == 0) {
					tmp = subWord(rotWord(tmp));
					/*
					 *	& 0xff needed because rcon will return a signed int
					 */
					tmp[0] = (byte) (tmp[0] ^ rcon[i / nk] & 0xff);
				} else if ((nk > 6) && (i % nk == 4)) {
					tmp = subWord(tmp);
				}
			} catch (Exception e) {
				return null;
			}

			toReturn[i] = xor(toReturn[i - nk], tmp);
			i++;
		}

		return toReturn;
	}

	/*
	 * Transformation in the cipher and inverse cipher in which a Round key is
	 * added to the state using a XOR op
	 * 
	 * the argument sw is the key schedule words from key expansion
	 */
	private static byte[][] addRoundKey(byte[][] state, byte[][] sw, int round) {
		byte[][] toReturn = new byte[4][nb];
		for (int c = 0; c < 4; c++) {
			for (int i = 0; i < nb; i++) {
				toReturn[i][c] = (byte) (state[i][c] ^ sw[round * nb + c][i]);
			}
		}

		return toReturn;
	}

	/*
	 * Source: page 18 FIPS 197
	 */
	private static byte[][] mixCloumns(byte[][] s) {
		int[] sp = new int[4];
		for (int c = 0; c < 4; c++) {
			sp[0] = finiteFieldMult((byte) 0x02, s[0][c]) ^ finiteFieldMult((byte) 0x03, s[1][c]) ^ s[2][c] ^ s[3][c];
			sp[1] = s[0][c] ^ finiteFieldMult((byte) 0x02, s[1][c]) ^ finiteFieldMult((byte) 0x03, s[2][c]) ^ s[3][c];
			sp[2] = s[0][c] ^ s[1][c] ^ finiteFieldMult((byte) 0x02, s[2][c]) ^ finiteFieldMult((byte) 0x03, s[3][c]);
			sp[3] = finiteFieldMult((byte) 0x03, s[0][c]) ^ s[1][c] ^ s[2][c] ^ finiteFieldMult((byte) 0x02, s[3][c]);
			for (int i = 0; i < 4; i++) {
				s[i][c] = (byte) (sp[i]);
			}
		}

		return s;
	}

	/*
	 * Source: page 23 FIPS 197
	 */
	private static byte[][] invMixColumns(byte[][] s) {
		int[] sp = new int[4];
		byte b02 = (byte) 0x0e, b03 = (byte) 0x0b, b04 = (byte) 0x0d, b05 = (byte) 0x09;
		for (int c = 0; c < 4; c++) {
			sp[0] = finiteFieldMult(b02, s[0][c]) ^ finiteFieldMult(b03, s[1][c]) ^ finiteFieldMult(b04, s[2][c])
					^ finiteFieldMult(b05, s[3][c]);
			sp[1] = finiteFieldMult(b05, s[0][c]) ^ finiteFieldMult(b02, s[1][c]) ^ finiteFieldMult(b03, s[2][c])
					^ finiteFieldMult(b04, s[3][c]);
			sp[2] = finiteFieldMult(b04, s[0][c]) ^ finiteFieldMult(b05, s[1][c]) ^ finiteFieldMult(b02, s[2][c])
					^ finiteFieldMult(b03, s[3][c]);
			sp[3] = finiteFieldMult(b03, s[0][c]) ^ finiteFieldMult(b04, s[1][c]) ^ finiteFieldMult(b05, s[2][c])
					^ finiteFieldMult(b02, s[3][c]);
			for (int i = 0; i < 4; i++)
				s[i][c] = (byte) (sp[i]);
		}

		return s;
	}

	/*
	 * Source: figure 8 FIPS 197
	 */
	private static byte[][] shiftRows(byte[][] state) {
		byte[][] toReturn = new byte[4][nb];
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < nb; c++) {
				toReturn[r][c] = state[r][(c + shift(r, nb)) % nb];
			}
		}
		return toReturn;
	}

	/*
	 * Inverse of shiftRows()
	 */
	private static byte[][] invShiftRows(byte[][] state) {
		byte[] t = new byte[4];
		for (int r = 1; r < 4; r++) {
			for (int c = 0; c < nb; c++) {
				t[(c + r) % nb] = state[r][c];
			}
			for (int c = 0; c < nb; c++) {
				state[r][c] = t[c];
			}
		}
		return state;
	}

	/*
	 * Inverse of subBytes()
	 */
	private static byte[][] invSubBytes(byte[][] state) {
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < nb; col++) {
				state[row][col] = (byte) (inv_sbox[(state[row][col] & 0x000000ff)] & 0xff);
			}
		}
		return state;
	}

	/*
	 * helpers
	 */
	private static byte[] xor(byte[] in1, byte[] in2) {
		byte[] toReturn = new byte[in1.length];
		for (int i = 0; i < in1.length; i++) {
			toReturn[i] = (byte) (in1[i] ^ in2[i]);
		}
		return toReturn;
	}

	/*
	 * Source: http://www.cs.utsa.edu/~wagner/laws/FFM.html
	 */
	private static byte finiteFieldMult(byte a, byte b) {
		byte aa = a, bb = b, r = 0, t;
		while (aa != 0) {
			if ((aa & 1) != 0)
				r = (byte) (r ^ bb);
			t = (byte) (bb & 0x80);
			bb = (byte) (bb << 1);
			if (t != 0)
				bb = (byte) (bb ^ 0x1b);
			aa = (byte) ((aa & 0xff) >> 1);
		}
		return r;
	}

	/*
	 * Source: Section 5.1.2 FIPS 197
	 */
	private static int shift(int a, int b) {
		// Não faz sentido... mas é o que está no desenho do FIPS 197
		return a;
	}

	/*
	 * Convert byte[][] to byte[]
	 */
	private static byte[] bidimensional2unidimensional(byte[][] src) {
		byte[] toReturn = new byte[4 * nb];
		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i % 4 * 4 + i / 4] = src[i / 4][i % 4];
		}
		return toReturn;
	}

	/*
	 * Convert byte[] to byte[][]
	 */
	private static byte[][] unidimensional2bidimensional(byte[] src) {
		byte[][] toReturn = new byte[4][nb];
		for (int i = 0; i < src.length; i++) {
			toReturn[i / 4][i % 4] = src[i % 4 * 4 + i / 4];
		}
		return toReturn;
	}

	/*
	 * s-box is a substitution table used on subBytes() method Source: Figure 7
	 * - FIPS 197
	 */
	private static final int[] sbox = { 0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE,
			0xD7, 0xAB, 0x76, 0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72,
			0xC0, 0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15, 0x04,
			0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75, 0x09, 0x83, 0x2C,
			0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84, 0x53, 0xD1, 0x00, 0xED, 0x20,
			0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF, 0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33,
			0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8, 0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC,
			0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2, 0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E,
			0x3D, 0x64, 0x5D, 0x19, 0x73, 0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE,
			0x5E, 0x0B, 0xDB, 0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4,
			0x79, 0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08, 0xBA,
			0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A, 0x70, 0x3E, 0xB5,
			0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E, 0xE1, 0xF8, 0x98, 0x11, 0x69,
			0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF, 0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42,
			0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16 };

	/*
	 * inv s-box Source: Figure 14 - FIPS 197
	 */
	private static final int[] inv_sbox = { 0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5, 0x38, 0xBF, 0x40, 0xA3, 0x9E,
			0x81, 0xF3, 0xD7, 0xFB, 0x7C, 0xE3, 0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4, 0xDE,
			0xE9, 0xCB, 0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D, 0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E,
			0x08, 0x2E, 0xA1, 0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B, 0xD1, 0x25, 0x72, 0xF8,
			0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4, 0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92, 0x6C, 0x70, 0x48, 0x50,
			0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D, 0x84, 0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC,
			0xD3, 0x0A, 0xF7, 0xE4, 0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06, 0xD0, 0x2C, 0x1E, 0x8F, 0xCA, 0x3F, 0x0F, 0x02,
			0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B, 0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2,
			0xCF, 0xCE, 0xF0, 0xB4, 0xE6, 0x73, 0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD, 0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8,
			0x1C, 0x75, 0xDF, 0x6E, 0x47, 0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E, 0xAA, 0x18,
			0xBE, 0x1B, 0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79, 0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4,
			0x1F, 0xDD, 0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xEC, 0x5F, 0x60, 0x51,
			0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D, 0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF, 0xA0, 0xE0, 0x3B, 0x4D,
			0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53, 0x99, 0x61, 0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77,
			0xD6, 0x26, 0xE1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D };

	/*
	 * Round constant word array Source:
	 * https://en.wikipedia.org/wiki/Rijndael_key_schedule A table was chosen
	 * but some implementations explicitly calculate only the values that will
	 * be used: ex:
	 * https://github.com/golang/go/blob/master/src/crypto/aes/const.go defines
	 * the constant powx. and at
	 * https://github.com/golang/go/blob/master/src/crypto/aes/block.go#L142 it
	 * is defined the method expandKeyGo. in its implementation it is possible
	 * to see how the calculation is done using the powx constant.
	 */
	private static final int rcon[] = { 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8,
			0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91,
			0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74,
			0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a,
			0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4,
			0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d,
			0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc,
			0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61,
			0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04,
			0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97,
			0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25,
			0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
			0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4,
			0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33,
			0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d };

	/*
	 * Wrong word size exception message
	 */
	private static final String notFourByteWordExceptionMsg = "Word doesn't have 4 bytes";
}
