package controllers;

import models.SteganographyCore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SteganographyController {

    public static void embedTextIntoImage(BufferedImage image, String text, byte[][] keySchedule, String outputPath) throws IOException {
        // Encrypt the text
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedText = SteganographyCore.encryptText(textBytes, keySchedule);

        // Prepend length in binary to the encrypted text
        int dataLength = encryptedText.length * 8; // Calculate length in bits
        String lengthBinary = String.format("%32s", Integer.toBinaryString(dataLength)).replace(' ', '0');

        // Convert encrypted text to binary
        StringBuilder binaryData = new StringBuilder(lengthBinary);
        for (byte b : encryptedText) {
            binaryData.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        System.out.println("Data length: " + dataLength);
        System.out.println("Binary data to embed: " + binaryData);

        // Embed binary data into the blue channel of the image
        int bitIndex = 0;
        outerLoop:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int blue = (rgb & 0xFF);

                if (bitIndex < binaryData.length()) {
                    blue = (blue & 0xFE) | (binaryData.charAt(bitIndex++) - '0');
                }

                int modifiedRGB = (rgb & 0xFFFF00) | blue; // Update only the blue channel
                image.setRGB(x, y, modifiedRGB);

                if (bitIndex >= binaryData.length()) break outerLoop;
            }
        }

        // Save the modified image
        ImageIO.write(image, "png", new File(outputPath));
        System.out.println("Text successfully embedded into the image. Output saved to: " + outputPath);
    }

    public static String extractTextFromImage(BufferedImage image, byte[][] keySchedule) {
        StringBuilder binaryData = new StringBuilder();

        int x = 0, y = 0;
        while (binaryData.length() < 32) {
            int rgb = image.getRGB(x++, y);
            binaryData.append(rgb & 1); // Extract the LSB of the blue channel

            if (x >= image.getWidth()) {
                x = 0;
                y++;
            }
        }

        int dataLength = Integer.parseInt(binaryData.toString(), 2);
        System.out.println("Extracted data length: " + dataLength);

        binaryData.setLength(0);
        while (binaryData.length() < dataLength) {
            int rgb = image.getRGB(x++, y);
            binaryData.append(rgb & 1);

            if (x >= image.getWidth()) {
                x = 0;
                y++;
            }
        }

        // Convert binary data to byte array
        byte[] encryptedBytes = new byte[dataLength / 8];
        for (int i = 0; i < encryptedBytes.length; i++) {
            encryptedBytes[i] = (byte) Integer.parseInt(binaryData.substring(i * 8, (i + 1) * 8), 2);
        }

        System.out.println("Extracted encrypted bytes: " + Arrays.toString(encryptedBytes));

        // Decrypt the extracted data
        byte[] decryptedBytes = SteganographyCore.decryptText(encryptedBytes, keySchedule);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
