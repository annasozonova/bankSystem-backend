package com.annasozonova.bank.util;

/**
 * Utility for masking card numbers for secure display.
 */
public class MaskUtil {

    /**
     * Masks all but the last 4 digits of the provided card number.
     * If the number is too short or null, returns it unchanged.
     *
     * @param cardNumber raw card number (e.g., "1234567812345678")
     * @return masked number in format "**** **** **** 5678"
     */
    public static String mask(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
