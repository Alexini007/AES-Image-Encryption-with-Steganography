package shared;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class UtilFunctions {
    private static final int blockSizeInBytes = Constants.blockSizeInBytes; // AES block size is 16 bytes (128 bits)

    // Function to pad the data (encryption)
    public static byte[] padData(byte[] data) {
        int paddingLength = blockSizeInBytes - (data.length % blockSizeInBytes);
        byte[] paddedData = Arrays.copyOf(data, data.length + paddingLength);
        for (int i = data.length; i < paddedData.length; i++) {
            paddedData[i] = (byte) paddingLength;
        }
        return paddedData;
    }

    // Function to remove padding from decrypted data
    public static byte[] removePadding(byte[] data) {
        int paddingLength = data[data.length - 1] & 0xFF;
        return Arrays.copyOf(data, data.length - paddingLength);
    }

    // Function to add padding (text embedding/encoding)
    public static byte[] addExplicitPadding(byte[] input, int blockSize) {
        int paddingValue = blockSize - (input.length % blockSize); // Calculate padding length
        byte[] padded = Arrays.copyOf(input, input.length + paddingValue);
        padded[input.length] = (byte) 0x80;
        for (int i = input.length + 1; i < padded.length; i++) {
            padded[i] = (byte) 0x00;
        }
        return padded;
    }

    // Function to remove padding (text decoding)
    public static byte[] removeExplicitPadding(byte[] input) {
        int paddingIndex = -1;

        for (int i = input.length - 1; i >= 0; i--) {
            if (input[i] == (byte) 0x80) {
                paddingIndex = i;
                break;
            } else if (input[i] != 0) {
                throw new IllegalArgumentException("Invalid explicit padding format detected!");
            }
        }

        if (paddingIndex == -1) {
            throw new IllegalArgumentException("Padding delimiter not found!");
        }

        // Return the data without the padding
        return Arrays.copyOf(input, paddingIndex);
    }

    // Function to save image from byte array
    public static void saveImage(byte[] imageData, String filePath, String format) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        ImageIO.write(image, format, new File(filePath));
    }


}
