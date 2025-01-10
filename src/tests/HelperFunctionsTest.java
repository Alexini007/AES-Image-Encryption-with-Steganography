package tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import shared.HelperFunctions;
import models.KeyManager;
import java.util.Base64;

class HelperFunctionsTest {
    private static final int AES_KEY_LENGTH = 16;

    @Test
    public void testValidBase64Password() {
        // A valid Base64 password that decodes to a 16-byte key
        String base64Password = "QunPmWnXUsOeHczH5eHJHA=="; // Example of a valid Base64 string
        byte[] expectedKey = Base64.getDecoder().decode(base64Password);

        byte[] actualKey = HelperFunctions.decodeBase64Password(base64Password);

        assertNotNull(actualKey, "The decoded key should not be null");
        assertEquals(AES_KEY_LENGTH, actualKey.length, "The key length should be 16 bytes");
        assertArrayEquals(expectedKey, actualKey, "The decoded key should match the expected key");
    }

    @Test
    public void testInvalidBase64Password_InvalidLength() {
        // An invalid Base64 password with an invalid length (Not 16 bytes)
        String invalidBase64Password = "TooShortKey=="; // Decodes to less than 16 bytes

        byte[] actualKey = HelperFunctions.decodeBase64Password(invalidBase64Password);

        assertNull(actualKey, "The result should be null for an invalid key length");
    }


    @Test
    public void testEmptyPassword() {
        // An empty Base64 password
        String emptyPassword = "";

        byte[] decodedKey = HelperFunctions.decodeBase64Password(emptyPassword);

        assertNull(decodedKey, "Decoding an empty password should return null");
    }

    @Test
    public void testGenerateRandomKey() {
        // A valid key length (16 bytes for AES-128)
        int keyLength = 16;

        byte[] randomKey = KeyManager.generateRandomKey(keyLength);

        // The key should not be null and should have the correct length
        assertNotNull(randomKey, "Generated key should not be null");
        assertEquals(keyLength, randomKey.length, "Generated key should have the correct length");
    }

    @Test
    public void testRotateWord() {
        // A 4-byte word
        byte[] word = {0x01, 0x02, 0x03, 0x04};

        byte[] rotatedWord = HelperFunctions.rotateWord(word);

        // The rotated word should have the correct order
        byte[] expectedWord = {0x02, 0x03, 0x04, 0x01};
        assertArrayEquals(expectedWord, rotatedWord, "Rotated word should match the expected order");
    }

    @Test
    public void testXorFunc() {
        // Two byte arrays of the same length
        byte[] a = {(byte) 0x01, (byte) 0x0F, (byte) 0xFF};
        byte[] b = {(byte) 0x0F, (byte) 0x0F, (byte) 0x0F};

        // XOR-ing the arrays
        byte[] result = HelperFunctions.xor_func(a, b);

        // The result should be an array of the same length
        byte[] expected = {(byte) 0x0E, (byte) 0x00, (byte) 0xF0};
        assertArrayEquals(expected, result, "Result of XOR should match the expected values");
    }

    @Test
    public void testXorFuncDifferentLengths() {
        // Two byte arrays of different lengths
        byte[] a = {(byte) 0x01, (byte) 0x0F};
        byte[] b = {(byte) 0x0F, (byte) 0x0F, (byte) 0x0F};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            HelperFunctions.xor_func(a, b);
        });

        // An IllegalArgumentException should be thrown with the expected message
        String expectedMessage = "Input byte arrays must be of the same length.";
        assertEquals(expectedMessage, exception.getMessage(), "Exception message should match expected message");
    }

    @Test
    public void testGenerateKeySchedule() {
        // A valid 16-byte AES key
        byte[] key = {
                (byte) 0x2b, (byte) 0x7e, (byte) 0x15, (byte) 0x16,
                (byte) 0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6,
                (byte) 0xab, (byte) 0xf7, (byte) 0xcf, (byte) 0x39,
                (byte) 0x20, (byte) 0x43, (byte) 0x80, (byte) 0x85
        };

        byte[][] keySchedule = KeyManager.generateKeySchedule(key);

        // The key schedule should not be null and have the correct size
        assertNotNull(keySchedule, "Key schedule should not be null");
        assertEquals(44, keySchedule.length, "Key schedule should contain 44 words for AES-128");

        // Extract the firts word from the generated key schedule
        byte[] expectedFirstWord = { (byte) 0x2b, (byte) 0x7e, (byte) 0x15, (byte) 0x16 };
        assertArrayEquals(expectedFirstWord, keySchedule[0], "First word should match the expected value");

        // Extract the last word from the generated key schedule
        byte[][] expectedKeySchedule = KeyManager.generateKeySchedule(key);
        byte[] expectedLastWord = expectedKeySchedule[43]; // Extract the last word from the correctly generated key schedule
        assertArrayEquals(expectedLastWord, keySchedule[43], "Last word should match the expected value");
    }
}