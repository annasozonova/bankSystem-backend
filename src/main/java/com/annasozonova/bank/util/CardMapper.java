package com.annasozonova.bank.util;

import com.annasozonova.bank.dto.CardDto;
import com.annasozonova.bank.dto.CreateCardRequest;
import com.annasozonova.bank.model.Card;
import com.annasozonova.bank.model.CardStatus;
import com.annasozonova.bank.model.User;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Utility for converting between card-related entities and DTOs.
 */
public class CardMapper {

    /**
     * Converts {@link CreateCardRequest} and {@link User} into a {@link Card} entity.
     *
     * @param req   request data
     * @param owner owner of the card
     * @return card entity
     */
    public static Card toEntity(CreateCardRequest req, User owner) {
        String rawNumber = req.getNumber();
        byte[] encrypted = CardCryptoUtil.encrypt(rawNumber);
        String mask = MaskUtil.mask(rawNumber);

        BigDecimal balance = req.getInitialBalance() != null
                ? req.getInitialBalance()
                : BigDecimal.ZERO;

        OffsetDateTime now = OffsetDateTime.now();

        return Card.builder()
                .cardNumberEnc(encrypted)
                .cardMask(mask)
                .owner(owner)
                .expirationDate(req.getExpiryDate())
                .balance(balance)
                .status(CardStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Converts a {@link Card} entity into a {@link CardDto}.
     *
     * @param card the card entity
     * @return card DTO
     */
    public static CardDto toDto(Card card) {
        return new CardDto(
                card.getId(),
                card.getCardMask(),
                card.getExpirationDate(),
                card.getStatus().name(),
                card.getBalance()
        );
    }
}
