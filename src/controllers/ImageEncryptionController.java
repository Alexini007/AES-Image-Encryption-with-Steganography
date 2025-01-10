package controllers;

import shared.Constants;
import shared.HelperFunctions;
import shared.UtilFunctions;

import java.util.Arrays;

public class ImageEncryptionController {
    private static final int blockSizeInBytes = Constants.blockSizeInBytes; // AES block size is 16 bytes (128 bits)

    // Function to encrypt the entire image data with AES (CBC mode)
    public static byte[] encryptImage(byte[] imageData, byte[][] roundKeys, byte[] iv) {
        byte[] paddedData = UtilFunctions.padData(imageData);
        byte[] encryptedData = new byte[paddedData.length];

        byte[] previousBlock = iv;
        for (int i = 0; i < paddedData.length; i += blockSizeInBytes) {
            byte[] block = Arrays.copyOfRange(paddedData, i, i + blockSizeInBytes);
            byte[] xorBlock = HelperFunctions.xor_func(block, previousBlock); // CBC XOR step
            byte[] encryptedBlock = AESController.encryptBlock(xorBlock, roundKeys);

            System.arraycopy(encryptedBlock, 0, encryptedData, i, blockSizeInBytes);
            previousBlock = encryptedBlock;
        }

        return encryptedData;
    }

    // Function to decrypt the entire image data with AES (CBC mode)
    public static byte[] decryptImage(byte[] encryptedData, byte[][] roundKeys, byte[] iv) {

        // Ensure encryptedData length is a multiple of the block size
        if (encryptedData.length % blockSizeInBytes != 0) {
            throw new IllegalArgumentException("The input data length is not valid for decryption. It might not be encrypted.");
        }

        byte[] decryptedData = new byte[encryptedData.length];

        byte[] previousBlock = iv;
        for (int i = 0; i < encryptedData.length; i += blockSizeInBytes) {
            byte[] block = Arrays.copyOfRange(encryptedData, i, i + blockSizeInBytes);
            byte[] decryptedBlock = AESController.decryptBlock(block, roundKeys);
            byte[] originalBlock = HelperFunctions.xor_func(decryptedBlock, previousBlock);

            System.arraycopy(originalBlock, 0, decryptedData, i, blockSizeInBytes);
            previousBlock = block;
        }

        return UtilFunctions.removePadding(decryptedData);
    }
}
