package shared;

import shared.Constants;

import java.util.Base64;

public class HelperFunctions {
    private static final int blockSizeInBytes = Constants.blockSizeInBytes; // AES block size is 16 bytes (128 bits)
    private static final int[] sbox = Constants.SBOX;

    /**
     * Performs a XOR operation on two byte arrays.
     *
     * @param a (byte array)
     * @param b (byte array)
     * @return a new byte array that is a result of XOR of the elements from arrays a and b
     * @throws IllegalArgumentException if the param arrays are not of the same length
     */
    public static byte[] xor_func(byte[] a, byte[] b) {
        // Check if the two array bytes have the same length
        if (a.length != b.length) {
            throw new IllegalArgumentException("Input byte arrays must be of the same length.");
        }
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;
    }

    // Rotate a word (array of 4 bytes) (Used in the generateKeySchedule function)
    public static byte[] rotateWord(byte[] word) {
        byte[] result = new byte[4];
        result[0] = word[1];
        result[1] = word[2];
        result[2] = word[3];
        result[3] = word[0];
        return result;
    }

    // Substitute bytes using AES S-box (SubWord operation)
    public static byte[] subWord(byte[] word) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (sbox[word[i] & 0xFF]);
        }
        return result;
    }

    // Galois Field multiplication
    public static byte gmul(byte a, int b) {
        byte p = 0; // Product
        byte hiBitSet;

        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) {
                p ^= a;
            }

            hiBitSet = (byte) (a & 0x80);
            a <<= 1;
            if (hiBitSet != 0) {
                a ^= 0x1B;
            }

            b >>= 1;
        }
        return p;
    }

    public static byte[][] toStateMatrix(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("Input byte array must be exactly 16 bytes long.");
        }
        byte[][] state = new byte[4][4];
        for (int i = 0; i < 16; i++) {
            state[i % 4][i / 4] = bytes[i];
        }
        return state;
    }

    // Function that converts a state matrix back into a byte array
    public static byte[] fromStateMatrix(byte[][] state) {
        byte[] bytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            bytes[i] = state[i % 4][i / 4];
        }
        return bytes;
    }

    public static byte[] decodeBase64Password(String base64Password) {
        try {
            // Add padding to make the length a multiple of 4
            while (base64Password.length() % 4 != 0) {
                base64Password += "=";
            }
            byte[] aesKey = Base64.getDecoder().decode(base64Password);
            if (aesKey.length != blockSizeInBytes) {
                throw new IllegalArgumentException("Password must decode to a 16-byte key!");
            }
            return aesKey;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Base64 password or incorrect key length!");
            return null;
        }
    }

}
