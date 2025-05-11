package com.annasozonova.bank.controller;

import com.annasozonova.bank.dto.CardDto;
import com.annasozonova.bank.dto.CreateCardRequest;
import com.annasozonova.bank.dto.TransferRequest;
import com.annasozonova.bank.exception.ForbiddenOperationException;
import com.annasozonova.bank.model.CardStatus;
import com.annasozonova.bank.security.UserPrincipal;
import com.annasozonova.bank.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for managing bank cards. Supports creation, update, viewing,
 * deletion and fund transfers between cards.
 */
@RestController
@RequestMapping("/api/cards")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cards", description = "Operations related to bank cards")
public class CardController {
    private final CardService cardService;

    @Autowired
    public CardController(final CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Creates a new bank card. Accessible to administrators only.
     *
     * @param createCardRequest request body with card details
     * @return created card data
     */
    @Operation(summary = "Create a new card",
            description = "Accessible by ADMIN only")
    @PostMapping
    public ResponseEntity<CardDto> createCard(@RequestBody CreateCardRequest createCardRequest) {
        CardDto cardDto = cardService.createCard(createCardRequest);
        return ResponseEntity.ok(cardDto);
    }

    /**
     * Retrieves card details by ID. Users can access only their own cards.
     *
     * @param id unique identifier of the card
     * @return card data
     */
    @Operation(summary = "Get card by ID",
            description = "Accessible by USER or ADMIN")
    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    /**
     * Lists cards with optional filtering and pagination.
     * Admins see all cards; users see only their own.
     *
     * @param maskedNumber optional masked card number filter
     * @param status       optional card status filter
     * @param pageable     pagination parameters
     * @param principal    currently authenticated user
     * @return page of card data
     */
    @Operation(summary = "List cards with filtering and paging",
            description = "ADMIN: view all cards; USER: view only own cards")
    @GetMapping
    public ResponseEntity<Page<CardDto>> listCards(
            @RequestParam(required = false) String maskedNumber,
            @RequestParam(required = false) CardStatus status,
            @ParameterObject Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Page<CardDto> result = isAdmin
                ? cardService.getAllCards(pageable)
                : cardService.getUserCards(
                principal.getId(), maskedNumber, status, pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * Updates card details by ID. Accessible to administrators only.
     *
     * @param id  card ID
     * @param req new card data
     * @return updated card data
     */
    @Operation(summary = "Update card", description = "Accessible by ADMIN only")
    @PutMapping("/{id}")
    public ResponseEntity<CardDto> updateCard(
            @PathVariable UUID id,
            @RequestBody CreateCardRequest req) {
        return ResponseEntity.ok(cardService.updateCard(id, req));
    }

    /**
     * Deletes a card by ID. Accessible to administrators only.
     *
     * @param id card ID
     * @return 204 No Content if deletion is successful
     */
    @Operation(summary = "Delete card", description = "Accessible by ADMIN only")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Transfers funds between two cards owned by the same user.
     *
     * @param request   transfer request with source, destination and amount
     * @param principal current authenticated user
     * @return 200 OK if transfer is successful
     */
    @Operation(summary = "Transfer funds between own cards",
            description = "Accessible by USER only")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            throw new ForbiddenOperationException("Admins are not allowed to transfer funds");
        }

        cardService.transferFunds(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Requests blocking of a user's own card.
     *
     * @param id        card ID to block
     * @param principal current authenticated user
     * @return 204 No Content if successful
     */
    @Operation(summary = "Request blocking of own card",
            description = "Accessible by USER only")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> requestBlockCard(@PathVariable UUID id,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        cardService.requestBlockCard(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
