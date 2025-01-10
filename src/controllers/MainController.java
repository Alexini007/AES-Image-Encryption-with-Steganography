package controllers;

import models.KeyManager;
import shared.Constants;
import shared.HelperFunctions;
import shared.UtilFunctions;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.util.Arrays;


public class MainController {
    private static final int blockSizeInBytes = Constants.blockSizeInBytes; // AES block size is 16 bytes (128 bits)

    public static boolean buttonImageEncryption(String base64Password, String inputImagePath, String encryptedImagePath) {
        try {
            // Decode the Base64 password
            byte[] aesKey = HelperFunctions.decodeBase64Password(base64Password);
            if (aesKey == null) {
                ViewController.showAlert("Invalid password! Ensure it is a valid Base64 string and decodes to a 16-byte key.");
                return false;
            }

            // Generate round keys
            byte[][] roundKeys = KeyManager.generateKeySchedule(aesKey);

            // Read the original image
            BufferedImage originalImage = ImageIO.read(new File(inputImagePath));
            if (originalImage == null) {
                ViewController.showAlert("Failed to read the image file. Ensure it is a valid image.");
                return false; // Encryption failed
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // Encrypt the image
            byte[] iv = KeyManager.generateRandomIV();
            byte[] encryptedBytes = ImageEncryptionController.encryptImage(imageBytes, roundKeys, iv);

            // Save the encrypted image
            try (FileOutputStream fos = new FileOutputStream(encryptedImagePath)) {
                fos.write(iv); // Prepend IV
                fos.write(encryptedBytes);
            }

            return true; // Encryption successful
        } catch (IOException e) {
            ViewController.showAlert("Error during encryption: " + e.getMessage());
            System.out.println("Error during encryption: " + e.getMessage());
            return false; // Encryption failed
        }
    }

    public static boolean buttonImageDecryption(String base64Password, String encryptedFilePath, String outputFilePath) {
        try {
            // Decode the Base64 password
            byte[] aesKey = HelperFunctions.decodeBase64Password(base64Password);
            if (aesKey == null) {
                ViewController.showAlert("Invalid password.");
                return false;
            }

            // Generate round keys
            byte[][] roundKeys = KeyManager.generateKeySchedule(aesKey);

            // Read the encrypted file
            byte[] encryptedFileBytes = Files.readAllBytes(Paths.get(encryptedFilePath));
            byte[] iv = Arrays.copyOfRange(encryptedFileBytes, 0, blockSizeInBytes); // Extract IV
            byte[] encryptedData = Arrays.copyOfRange(encryptedFileBytes, blockSizeInBytes, encryptedFileBytes.length); // Extract encrypted data

            // Decrypt the image
            byte[] decryptedBytes = ImageEncryptionController.decryptImage(encryptedData, roundKeys, iv);

            // Validate if decrypted bytes form a valid image
            ByteArrayInputStream bais = new ByteArrayInputStream(decryptedBytes);
            BufferedImage decryptedImage = ImageIO.read(bais);

            if (decryptedImage == null) {
                ViewController.showAlert("Decryption failed. Incorrect password!");
                return false;
            }

            // Save the decrypted image
            UtilFunctions.saveImage(decryptedBytes, outputFilePath, "png");

            return true; // Decryption successful
        } catch (IOException e) {
            ViewController.showAlert("Decryption failed: Unable to process the file.");
            System.out.println("Error during decryption: " + e.getMessage());
            return false; // Decryption failed
        }
    }

    public static boolean buttonEncode(String base64Password, String inputImagePath, String textToEmbed, String outputImagePath) {
        try {
            // Decode the Base64 password
            byte[] aesKey = HelperFunctions.decodeBase64Password(base64Password);
            if (aesKey == null) {
                ViewController.showAlert("Invalid password! Ensure it is a valid Base64 string and decodes to a 16-byte key.");
                return false;
            }

            // Generate round keys
            byte[][] roundKeys = KeyManager.generateKeySchedule(aesKey);

            // Load the input image
            BufferedImage image = ImageIO.read(new File(inputImagePath));
            if (image == null) {
                ViewController.showAlert("Failed to read the image file. Ensure it is a valid image.");
                return false; // Encoding failed
            }

            // Embed text into the image
            SteganographyController.embedTextIntoImage(image, textToEmbed, roundKeys, outputImagePath);

            return true; // Encoding successful
        } catch (IOException e) {
            ViewController.showAlert("Error during text embedding: " + e.getMessage());
            System.out.println("Error during text embedding: " + e.getMessage());
            return false; // Encoding failed
        }
    }


    public static String buttonDecode(String base64Password, String embeddedImagePath) {
        try {
            // Decode the Base64 password
            byte[] aesKey = HelperFunctions.decodeBase64Password(base64Password);
            if (aesKey == null) {
                ViewController.showAlert("Invalid password! Ensure it is a valid Base64 string and decodes to a 16-byte key.");
                return null;
            }

            // Generate round keys
            byte[][] roundKeys = KeyManager.generateKeySchedule(aesKey);

            // Load the image with the embedded text
            BufferedImage image = ImageIO.read(new File(embeddedImagePath));
            if (image == null) {
                ViewController.showAlert("Failed to read the image file. Ensure it is a valid image.");
                return null;
            }

            // Extract text from the image
            String extractedText = SteganographyController.extractTextFromImage(image, roundKeys);
            if (extractedText == null || extractedText.isEmpty()) {
                ViewController.showAlert("No text found in the image. It may not contain embedded data or the password is incorrect.");
                return null;
            }
            return extractedText;
        } catch (IOException e) {
            ViewController.showAlert("Error during text extraction: " + e.getMessage());
            System.out.println("Error during text extraction: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            ViewController.showAlert("Decryption failed: " + e.getMessage());
            System.out.println("Decryption failed: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
    }
}
