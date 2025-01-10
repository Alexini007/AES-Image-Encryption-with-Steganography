package controllers;

import shared.Constants;
import models.AESCore;
import shared.HelperFunctions;
import shared.UtilFunctions;

public class AESController {
    private static final int blockSizeInBytes = Constants.blockSizeInBytes; // AES block size is 16 bytes (128 bits)
    private static final int numberOfRounds = Constants.numberOfRounds;   // Number of rounds of encryption (10 for AES-128)


    // Function to perform all AES encryption rounds
    public static byte[][] aesEncrypt(byte[][] state, byte[][] roundKeys) {
        // Initial round - AddRoundKey with the initial key (Round 0)
        state = AESCore.AddRoundKey(state, roundKeys, 0);

        // 9 rounds of SubBytes, ShiftRows, MixColumns, and AddRoundKey (1 to numberOfRounds - 1)
        for (int round = 1; round <= numberOfRounds; round++) {
            state = AESCore.SubBytes(state);
            state = AESCore.shiftRows(state);
            if (round < numberOfRounds) {
                state = AESCore.mixColumns(state);
            }
            state = AESCore.AddRoundKey(state, roundKeys, round);
        }

        return state; // Encrypted state
    }

    // Function to perform all AES decryption rounds
    public static byte[][] aesDecrypt(byte[][] state, byte[][] roundKeys) {
        // Initial AddRoundKey with the last round key
        state = AESCore.AddRoundKey(state, roundKeys, numberOfRounds);

        // Loop for decryption rounds
        for (int round = numberOfRounds - 1; round >= 0; round--) {
            state = AESCore.InvShiftRows(state);
            state = AESCore.InvSubBytes(state);
            state = AESCore.AddRoundKey(state, roundKeys, round);

            if (round > 0) { // Skip InvMixColumns for the final round
                state = AESCore.InvMixColumns(state);
            }
        }

        return state; // Decrypted state
    }

    // Encrypt a single block (16 bytes) with AES
    public static byte[] encryptBlock(byte[] block, byte[][] roundKeys) {
        byte[][] state = new byte[4][4];
        // Convert the block into a state matrix
        for (int i = 0; i < blockSizeInBytes; i++) {
            state[i % 4][i / 4] = block[i];
        }

        state = AESController.aesEncrypt(state, roundKeys);

        // Convert the state matrix back to a block
        byte[] encryptedBlock = new byte[blockSizeInBytes];
        for (int i = 0; i < blockSizeInBytes; i++) {
            encryptedBlock[i] = state[i % 4][i / 4];
        }
        return encryptedBlock;
    }

    // Decrypt a single block (16 bytes) with AES
    public static byte[] decryptBlock(byte[] block, byte[][] roundKeys) {
        byte[][] state = new byte[4][4];
        // Convert the block into a state matrix
        for (int i = 0; i < blockSizeInBytes; i++) {
            state[i % 4][i / 4] = block[i];
        }

        state = AESController.aesDecrypt(state, roundKeys);

        // Convert the state matrix back to a block
        byte[] decryptedBlock = new byte[blockSizeInBytes];
        for (int i = 0; i < blockSizeInBytes; i++) {
            decryptedBlock[i] = state[i % 4][i / 4];
        }
        return decryptedBlock;
    }
}
