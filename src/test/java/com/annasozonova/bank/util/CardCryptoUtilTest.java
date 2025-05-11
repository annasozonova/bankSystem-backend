package com.annasozonova.bank.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link CardCryptoUtil} encryption and decryption.
 */
class CardCryptoUtilTest {

    @Test
    void shouldEncryptAndDecryptCardNumber() {
        String original = "1234567812345678";

        byte[] encrypted = CardCryptoUtil.encrypt(original);
        assertNotNull(encrypted, "Encrypted output should not be null");
        assertNotEquals(original, new String(encrypted), "Encrypted data should differ from input");

        String decrypted = CardCryptoUtil.decrypt(encrypted);
        assertEquals(original, decrypted, "Decrypted value should match original");
    }
}