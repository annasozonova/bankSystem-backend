package com.annasozonova.bank.service.impl;

import com.annasozonova.bank.dto.*;
import com.annasozonova.bank.exception.BusinessException;
import com.annasozonova.bank.exception.ForbiddenOperationException;
import com.annasozonova.bank.exception.ResourceNotFoundException;
import com.annasozonova.bank.model.Card;
import com.annasozonova.bank.model.CardStatus;
import com.annasozonova.bank.model.User;
import com.annasozonova.bank.repository.CardRepository;
import com.annasozonova.bank.repository.UserRepository;
import com.annasozonova.bank.service.CardService;
import com.annasozonova.bank.util.CardMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * {@link CardService} implementation that handles business logic
 * for creating, updating, deleting, and querying bank cards.
 */
@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepo;
    private final UserRepository userRepo;

    @Autowired
    public CardServiceImpl(CardRepository cardRepo, UserRepository userRepo) {
        this.cardRepo = cardRepo;
        this.userRepo = userRepo;
    }

    /**
     * Creates a new card for the given user.
     *
     * @param req creation request
     * @return created card
     * @throws ResourceNotFoundException if user does not exist
     */
    @Override
    public CardDto createCard(CreateCardRequest req) {
        User owner = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + req.getUserId()));
        Card entity = CardMapper.toEntity(req, owner);
        return CardMapper.toDto(cardRepo.save(entity));
    }

    /**
     * Sets the card status to BLOCKED.
     *
     * @param cardID ID of the card
     * @throws ResourceNotFoundException if card does not exist
     */
    @Override
    public void blockCard(UUID cardID) {
        Card card = cardRepo.findById(cardID)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + cardID));
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    /**
     * Sets the card status to ACTIVE.
     *
     * @param cardId ID of the card
     * @throws ResourceNotFoundException if card does not exist
     */
    @Override
    public void activateCard(UUID cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + cardId));
        card.setStatus(CardStatus.ACTIVE);
        cardRepo.save(card);
    }

    /**
     * Deletes the card by ID.
     *
     * @param cardId ID of the card
     * @throws ResourceNotFoundException if card does not exist
     */
    @Override
    public void deleteCard(UUID cardId) {
        try {
            cardRepo.deleteById(cardId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("Card not found with id " + cardId);
        }
    }

    /**
     * Returns all cards with pagination.
     *
     * @param pageable pagination options
     * @return paginated list of cards
     */
    @Override
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepo.findAll(pageable).map(CardMapper::toDto);
    }

    /**
     * Returns user cards with optional filters.
     *
     * @param userId       ID of the user
     * @param maskedNumber optional card mask filter
     * @param status       optional card status
     * @param pageable     pagination options
     * @return paginated list of matching cards
     * @throws ResourceNotFoundException if user does not exist
     */
    @Override
    public Page<CardDto> getUserCards(UUID userId, String maskedNumber, CardStatus status, Pageable pageable) {
        userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        Page<Card> page = switch ((maskedNumber != null ? 1 : 0) + (status != null ? 2 : 0)) {
            case 3 -> cardRepo.findByOwner_IdAndCardMaskContainingIgnoreCaseAndStatus(userId, maskedNumber, status, pageable);
            case 1 -> cardRepo.findByOwner_IdAndCardMaskContainingIgnoreCase(userId, maskedNumber, pageable);
            case 2 -> cardRepo.findByOwner_IdAndStatus(userId, status, pageable);
            default -> cardRepo.findByOwner_Id(userId, pageable);
        };

        return page.map(CardMapper::toDto);
    }

    /**
     * Allows a user to request blocking their own card.
     *
     * @param userId ID of the user
     * @param cardId ID of the card
     * @throws ResourceNotFoundException   if card does not exist
     * @throws ForbiddenOperationException if card does not belong to user
     */
    @Override
    public void requestBlockCard(UUID userId, UUID cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + cardId));
        if (!card.getOwner().getId().equals(userId)) {
            throw new ForbiddenOperationException("You are not allowed to block this card");
        }
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    /**
     * Performs an atomic funds transfer between two cards.
     *
     * @param request transfer details
     * @throws ResourceNotFoundException if either card does not exist
     * @throws BusinessException         if transfer is not allowed
     */
    @Override
    @Transactional
    public void transferFunds(TransferRequest request) {
        UUID fromCardId = request.getFromCardId();
        UUID toCardId = request.getToCardId();
        BigDecimal amount = request.getAmount();

        Card fromCard = cardRepo.findById(fromCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Source card not found"));
        Card toCard = cardRepo.findById(toCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Target card not found"));

        if (!fromCard.getOwner().getId().equals(toCard.getOwner().getId())) {
            throw new BusinessException("Cards do not belong to the same user");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("Both cards must be active");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient funds on source card");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepo.save(fromCard);
        cardRepo.save(toCard);
    }

    /**
     * Returns the balance of a user’s card.
     *
     * @param userId ID of the user
     * @param cardId ID of the card
     * @return card balance
     * @throws ResourceNotFoundException   if card does not exist
     * @throws ForbiddenOperationException if card does not belong to user
     */
    @Override
    public BigDecimal getCardBalance(UUID userId, UUID cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + cardId));
        if (!card.getOwner().getId().equals(userId)) {
            throw new ForbiddenOperationException("You are not allowed to view this card balance");
        }
        return card.getBalance();
    }

    /**
     * Returns a card by ID.
     *
     * @param id card ID
     * @return card data
     * @throws ResourceNotFoundException if card does not exist
     */
    @Override
    public CardDto getCardById(UUID id) {
        Card card = cardRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + id));
        return CardMapper.toDto(card);
    }

    /**
     * Updates basic fields of an existing card.
     *
     * @param id  card ID
     * @param req update request
     * @return updated card
     * @throws ResourceNotFoundException if card does not exist
     */
    @Override
    @Transactional
    public CardDto updateCard(UUID id, CreateCardRequest req) {
        Card card = cardRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + id));
        card.setCardMask(req.getNumber());
        card.setExpirationDate(req.getExpiryDate());
        card.setUpdatedAt(OffsetDateTime.now());
        return CardMapper.toDto(cardRepo.save(card));
    }
}
