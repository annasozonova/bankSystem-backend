package com.annasozonova.bank.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Utility class for encrypting and decrypting card numbers using AES.
 * <p><b>Note:</b> Uses a hardcoded symmetric key for simplicity.
 * This approach is not secure for production.</p>
 */
public class CardCryptoUtil {

    private static final String ALGORITHM = "AES";

    /**
     * 128-bit symmetric key (insecure: hardcoded).
     */
    private static final byte[] SECRET_KEY = "1234567890123456".getBytes();

    private static Key getKey() {
        return new SecretKeySpec(SECRET_KEY, ALGORITHM);
    }

    /**
     * Encrypts the given plain text using AES.
     *
     * @param plainText raw card number
     * @return encrypted byte array
     * @throws RuntimeException if encryption fails
     */
    public static byte[] encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return cipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    /**
     * Decrypts the given AES-encrypted byte array.
     *
     * @param cipherBytes encrypted card number
     * @return decrypted plain text
     * @throws RuntimeException if decryption fails
     */
    public static String decrypt(byte[] cipherBytes) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return new String(cipher.doFinal(cipherBytes));
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}