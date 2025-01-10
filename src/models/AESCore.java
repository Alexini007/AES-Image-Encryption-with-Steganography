package models;

import shared.Constants;
import shared.HelperFunctions;

public class AESCore {

    /**
     * Performs the AddRoundKey step in the AES encryption process
     *
     * XORs each byte of the state with the round key
     *
     * @param state (A 4x4 matrix)
     * @param keySchedule (The full key schedule, a 2D array)
     * @param round (The current round of encryption/decryption)
     * @return A new 4x4 matrix representing the result of XORing the state matrix with the round key
     */
    public static byte[][] AddRoundKey(byte[][] state, byte[][] keySchedule, int round) {
        byte[][] result = new byte[4][4]; // Create a new matrix to store results
        int offset = round * 4; // Calculate offset for the round in the key schedule

        // XOR each byte of the state with the corresponding round key
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                result[row][col] = (byte) (state[row][col] ^ keySchedule[offset + col][row]);
            }
        }
        return result;
    }

    /**
     * Applies the SubBytes transformation to the state matrix
     *
     * @param state (A 4x4 byte matrix)
     * @return A new 4x4 byte matrix where each byte is substituted wit values from the AES S-Box
     */
    public static byte[][] SubBytes(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row][col] = (byte) Constants.SBOX[state[row][col] & 0xFF];
            }
        }
        return result;
    }

    public static byte[][] shiftRows(byte[][] state) {
        for (int row = 1; row < 4; row++) { // Start from the second row
            byte[] shiftedRow = new byte[4];

            // Move the end part of the row to the beginning
            System.arraycopy(state[row], row, shiftedRow, 0, 4 - row);

            // Move the beginning part of the row to the end
            System.arraycopy(state[row], 0, shiftedRow, 4 - row, row);

            // Directly assign the shifted row back to state[row]
            state[row] = shiftedRow;
        }
        return state;
    }

    public static byte[][] mixColumns(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int col = 0; col < 4; col++) {
            result[0][col] = (byte) (HelperFunctions.gmul(state[0][col], 2) ^ HelperFunctions.gmul(state[1][col], 3) ^ state[2][col] ^ state[3][col]);
            result[1][col] = (byte) (state[0][col] ^ HelperFunctions.gmul(state[1][col], 2) ^ HelperFunctions.gmul(state[2][col], 3) ^ state[3][col]);
            result[2][col] = (byte) (state[0][col] ^ state[1][col] ^ HelperFunctions.gmul(state[2][col], 2) ^ HelperFunctions.gmul(state[3][col], 3));
            result[3][col] = (byte) (HelperFunctions.gmul(state[0][col], 3) ^ state[1][col] ^ state[2][col] ^ HelperFunctions.gmul(state[3][col], 2));
        }
        return result;
    }

    // Function that applies inverse S-box to each byte
    public static byte[][] InvSubBytes(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row][col] = (byte) Constants.INV_SBOX[state[row][col] & 0xFF];
            }
        }
        return result;
    }

    // Function that shifts each row in the opposite direction
    public static byte[][] InvShiftRows(byte[][] state) {
        for (int row = 1; row < 4; row++) {
            byte[] shiftedRow = new byte[4];

            // Shift to the right for decryption
            System.arraycopy(state[row], 0, shiftedRow, row, 4 - row);
            System.arraycopy(state[row], 4 - row, shiftedRow, 0, row);

            state[row] = shiftedRow;
        }
        return state;
    }

    // Function that reverses MixColumns operation
    public static byte[][] InvMixColumns(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int col = 0; col < 4; col++) {
            result[0][col] = (byte) (HelperFunctions.gmul(state[0][col], 0x0E) ^ HelperFunctions.gmul(state[1][col], 0x0B) ^ HelperFunctions.gmul(state[2][col], 0x0D) ^ HelperFunctions.gmul(state[3][col], 0x09));
            result[1][col] = (byte) (HelperFunctions.gmul(state[0][col], 0x09) ^ HelperFunctions.gmul(state[1][col], 0x0E) ^ HelperFunctions.gmul(state[2][col], 0x0B) ^ HelperFunctions.gmul(state[3][col], 0x0D));
            result[2][col] = (byte) (HelperFunctions.gmul(state[0][col], 0x0D) ^ HelperFunctions.gmul(state[1][col], 0x09) ^ HelperFunctions.gmul(state[2][col], 0x0E) ^ HelperFunctions.gmul(state[3][col], 0x0B));
            result[3][col] = (byte) (HelperFunctions.gmul(state[0][col], 0x0B) ^ HelperFunctions.gmul(state[1][col], 0x0D) ^ HelperFunctions.gmul(state[2][col], 0x09) ^ HelperFunctions.gmul(state[3][col], 0x0E));
        }
        return result;
    }
}
