package com.annasozonova.bank.repository;

import com.annasozonova.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing {@link Transaction} entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Retrieves all transactions where the given card is either the source or the target.
     *
     * @param fromCardId UUID of the source card
     * @param toCardId   UUID of the target card
     * @return list of matching transactions
     */
    List<Transaction> findAllByFromCardIdOrToCardId(UUID fromCardId, UUID toCardId);
}
