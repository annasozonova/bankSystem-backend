package com.annasozonova.bank.repository;

import com.annasozonova.bank.model.Card;
import com.annasozonova.bank.model.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for CRUD operations on {@link Card} entities
 * and parameterized search by owner, mask, and status.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    /**
     * Retrieves a paginated list of cards owned by a specific user.
     */
    Page<Card> findByOwner_Id(UUID ownerId, Pageable pageable);

    /**
     * Retrieves a paginated list of cards for a user, filtering by card mask (partial match, case-insensitive).
     */
    Page<Card> findByOwner_IdAndCardMaskContainingIgnoreCase(
            UUID ownerId,
            String cardMask,
            Pageable pageable
    );

    /**
     * Retrieves a paginated list of cards for a user, filtering by status.
     */
    Page<Card> findByOwner_IdAndStatus(
            UUID ownerId,
            CardStatus status,
            Pageable pageable
    );

    /**
     * Retrieves a paginated list of cards for a user, filtering by both mask and status.
     */
    Page<Card> findByOwner_IdAndCardMaskContainingIgnoreCaseAndStatus(
            UUID ownerId,
            String cardMask,
            CardStatus status,
            Pageable pageable
    );
}
