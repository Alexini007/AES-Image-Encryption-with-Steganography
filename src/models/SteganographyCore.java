package models;

import shared.HelperFunctions;
import shared.UtilFunctions;

import java.util.Arrays;

public class SteganographyCore {

    // AES encryption function for the embedded text
    public static byte[] encryptText(byte[] textBytes, byte[][] keySchedule) {
        // Apply padding
        byte[] paddedBytes = UtilFunctions.addExplicitPadding(textBytes, 16);

        // Encrypt each block
        byte[] encryptedBytes = new byte[paddedBytes.length];
        for (int block = 0; block < paddedBytes.length / 16; block++) {
            byte[][] state = HelperFunctions.toStateMatrix(Arrays.copyOfRange(paddedBytes, block * 16, (block + 1) * 16));
            state = AESCore.AddRoundKey(state, keySchedule, 0);
            for (int round = 1; round < 10; round++) {
                state = AESCore.SubBytes(state);
                state = AESCore.shiftRows(state);
                state = AESCore.mixColumns(state);
                state = AESCore.AddRoundKey(state, keySchedule, round);
            }
            state = AESCore.SubBytes(state);
            state = AESCore.shiftRows(state);
            state = AESCore.AddRoundKey(state, keySchedule, 10);

            byte[] encryptedBlock = HelperFunctions.fromStateMatrix(state);
            System.arraycopy(encryptedBlock, 0, encryptedBytes, block * 16, 16);

        }
        return encryptedBytes;
    }

    // AES decryption function for the extracted text
    public static byte[] decryptText(byte[] encryptedBytes, byte[][] keySchedule) {
        byte[] decryptedBytes = new byte[encryptedBytes.length];

        for (int block = 0; block < encryptedBytes.length / 16; block++) {
            byte[][] state = HelperFunctions.toStateMatrix(Arrays.copyOfRange(encryptedBytes, block * 16, (block + 1) * 16));
            state = AESCore.AddRoundKey(state, keySchedule, 10);
            for (int round = 9; round > 0; round--) {
                state = AESCore.InvShiftRows(state);
                state = AESCore.InvSubBytes(state);
                state = AESCore.AddRoundKey(state, keySchedule, round);
                state = AESCore.InvMixColumns(state);
            }
            state = AESCore.InvShiftRows(state);
            state = AESCore.InvSubBytes(state);
            state = AESCore.AddRoundKey(state, keySchedule, 0);

            byte[] decryptedBlock = HelperFunctions.fromStateMatrix(state);
            System.arraycopy(decryptedBlock, 0, decryptedBytes, block * 16, 16);
        }

        // Remove padding
        byte[] unpaddedBytes = UtilFunctions.removeExplicitPadding(decryptedBytes);

        System.out.println("Decrypted plaintext bytes: " + Arrays.toString(unpaddedBytes));
        return unpaddedBytes;
    }

}
