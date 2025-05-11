package com.annasozonova.bank.service;

import com.annasozonova.bank.dto.CardDto;
import com.annasozonova.bank.dto.CreateCardRequest;
import com.annasozonova.bank.dto.TransferRequest;
import com.annasozonova.bank.exception.BusinessException;
import com.annasozonova.bank.exception.ForbiddenOperationException;
import com.annasozonova.bank.exception.ResourceNotFoundException;
import com.annasozonova.bank.model.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service interface for managing bank cards.
 */
public interface CardService {

    /**
     * Creates a new bank card.
     * Accessible by ADMIN.
     *
     * @param request card creation request
     * @return created card
     */
    CardDto createCard(CreateCardRequest request);

    /**
     * Blocks a card by ID.
     * Accessible by ADMIN or internally via requestBlockCard().
     *
     * @param cardID ID of the card to block
     * @throws ResourceNotFoundException if the card does not exist
     */
    void blockCard(UUID cardID);

    /**
     * Activates a previously blocked card.
     * Accessible by ADMIN.
     *
     * @param cardId ID of the card to activate
     * @throws ResourceNotFoundException if the card does not exist
     */
    void activateCard(UUID cardId);

    /**
     * Deletes a card by ID.
     * Accessible by ADMIN.
     *
     * @param cardId ID of the card to delete
     * @throws ResourceNotFoundException if the card does not exist
     */
    void deleteCard(UUID cardId);

    /**
     * Returns all cards with pagination.
     * Accessible by ADMIN.
     *
     * @param pageable pagination settings
     * @return paginated list of cards
     */
    Page<CardDto> getAllCards(Pageable pageable);

    /**
     * Returns cards of a specific user with optional filters.
     *
     * @param userId       ID of the card owner
     * @param maskedNumber optional card number mask
     * @param status       optional card status filter
     * @param pageable     pagination settings
     * @return paginated list of user cards
     * @throws ResourceNotFoundException if the user does not exist
     */
    Page<CardDto> getUserCards(UUID userId, String maskedNumber, CardStatus status, Pageable pageable);

    /**
     * Allows a user to request blocking of their own card.
     *
     * @param userId ID of the requesting user
     * @param cardId ID of the card to block
     * @throws ResourceNotFoundException   if the card does not exist
     * @throws ForbiddenOperationException if the card does not belong to the user
     */
    void requestBlockCard(UUID userId, UUID cardId);

    /**
     * Transfers funds between two cards of the same user.
     * Must be atomic.
     *
     * @param request transfer details
     * @throws ResourceNotFoundException if either card does not exist
     * @throws BusinessException         if funds are insufficient
     */
    void transferFunds(TransferRequest request);

    /**
     * Returns the balance of a specific card.
     *
     * @param userId ID of the card owner
     * @param cardId ID of the card
     * @return current balance
     * @throws ResourceNotFoundException   if the card does not exist
     * @throws ForbiddenOperationException if the card does not belong to the user
     */
    BigDecimal getCardBalance(UUID userId, UUID cardId);

    /**
     * Returns a card by ID.
     *
     * @param id card ID
     * @return found card
     * @throws ResourceNotFoundException if the card does not exist
     */
    CardDto getCardById(UUID id);

    /**
     * Updates card details.
     *
     * @param id  card ID
     * @param req new card data
     * @return updated card
     * @throws ResourceNotFoundException if the card does not exist
     */
    CardDto updateCard(UUID id, CreateCardRequest req);
}
