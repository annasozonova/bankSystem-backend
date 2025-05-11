package com.annasozonova.bank.model;

/**
 * Enum representing possible statuses of a bank card.
 */
public enum CardStatus {

    /**
     * Card is valid and can be used.
     */
    ACTIVE,

    /**
     * Card is temporarily blocked (by user or system).
     */
    BLOCKED,

    /**
     * Card has passed its expiration date.
     */
    EXPIRED
}
