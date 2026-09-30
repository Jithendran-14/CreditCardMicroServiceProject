package com.ofss.service;

import com.ofss.client.CustomerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ofss.dto.CustomerResponse;
import com.ofss.entity.CreditCard;
import com.ofss.exception.ResourceNotFoundException;
import com.ofss.repository.CreditCardRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.ofss.dto.CustomerOutstandingReport;
@Service
public class CreditCardService {
	private static final Logger logger =
	        LoggerFactory.getLogger(CreditCardService.class);

    private final CreditCardRepository creditCardRepository;
    private final CustomerClient customerClient;

    public CreditCardService(
            CreditCardRepository creditCardRepository,
            CustomerClient customerClient) {

        this.creditCardRepository = creditCardRepository;
        this.customerClient = customerClient;
    }


    // =========================================================
    // ISSUE CARD
    // =========================================================

    public CreditCard issueCard(CreditCard card) {

        // Validate customer
        if (card.getCustomerId() == null) {
            throw new IllegalArgumentException(
                    "Customer ID is required"
            );
        }

        CustomerResponse customer =
                customerClient.getCustomerById(
                        card.getCustomerId()
                );

        if (customer == null) {
            throw new ResourceNotFoundException(
                    "Customer not found with id: "
                            + card.getCustomerId()
            );
        }


        // Validate card number
        if (card.getCardNumber() == null ||
                card.getCardNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Card number is required"
            );
        }


        if (creditCardRepository.existsByCardNumber(
                card.getCardNumber())) {

            throw new IllegalArgumentException(
                    "Card number already exists"
            );
        }


        // Validate card type
        String cardType =
                normalizeCardType(card.getCardType());

        if (!isValidCardType(cardType)) {

            throw new IllegalArgumentException(
                    "Card type must be SILVER, GOLD or PLATINUM"
            );
        }


        // Validate credit limit
        if (card.getCreditLimit() == null ||
                card.getCreditLimit()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Credit limit must be greater than zero"
            );
        }


        // Validate expiry date
        if (card.getExpiryDate() == null ||
                card.getExpiryDate()
                        .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Expiry date must be in the future"
            );
        }


        // Set initial values
        card.setCardType(cardType);

        card.setAvailableCredit(
                card.getCreditLimit()
        );

        card.setOutstandingAmount(
                BigDecimal.ZERO
        );

        card.setCardStatus("ACTIVE");


        CreditCard savedCard = creditCardRepository.save(card);

        logger.info(
                "Card issued successfully: cardId={}, customerId={}, cardType={}",
                savedCard.getCardId(),
                savedCard.getCustomerId(),
                savedCard.getCardType()
        );

        return savedCard;
    }


    // =========================================================
    // GET ALL CARDS
    // =========================================================

    public List<CreditCard> getAllCards() {

        return creditCardRepository.findAll();
    }


    // =========================================================
    // GET CARD BY ID
    // =========================================================

    public CreditCard getCardById(Long cardId) {

        return creditCardRepository
                .findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Credit card not found with id: "
                                        + cardId
                        )
                );
    }


    // =========================================================
    // GET CARDS BY CUSTOMER
    // =========================================================

    public List<CreditCard> getCardsByCustomerId(
            Long customerId) {

        return creditCardRepository
                .findByCustomerId(customerId);
    }


    // =========================================================
    // UPDATE CARD
    // =========================================================

    public CreditCard updateCard(
            Long cardId,
            CreditCard updatedCard) {

        CreditCard existingCard =
                creditCardRepository
                        .findById(cardId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Credit card not found with id: "
                                                + cardId
                                )
                        );


        if (updatedCard.getCardType() != null) {

            String cardType =
                    normalizeCardType(
                            updatedCard.getCardType()
                    );

            if (!isValidCardType(cardType)) {

                throw new IllegalArgumentException(
                        "Card type must be SILVER, GOLD or PLATINUM"
                );
            }

            existingCard.setCardType(cardType);
        }


        if (updatedCard.getExpiryDate() != null) {

            if (updatedCard.getExpiryDate()
                    .isBefore(LocalDate.now())) {

                throw new IllegalArgumentException(
                        "Expiry date must be in the future"
                );
            }

            existingCard.setExpiryDate(
                    updatedCard.getExpiryDate()
            );
        }


        return creditCardRepository.save(existingCard);
    }


    // =========================================================
    // UPDATE CARD STATUS
    // =========================================================

    public CreditCard updateCardStatus(
            Long cardId,
            CreditCard updatedCard) {

        CreditCard existingCard =
                creditCardRepository
                        .findById(cardId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Credit card not found with id: "
                                                + cardId
                                )
                        );


        String status =
                normalizeStatus(
                        updatedCard.getCardStatus()
                );


        if (!"ACTIVE".equals(status) &&
                !"BLOCKED".equals(status)) {

            throw new IllegalArgumentException(
                    "Card status must be ACTIVE or BLOCKED"
            );
        }


        existingCard.setCardStatus(status);

        return creditCardRepository.save(existingCard);
    }


    // =========================================================
    // PROCESS PURCHASE
    // =========================================================
    @Transactional
    public CreditCard processPurchase(
            Long cardId,
            BigDecimal amount) {

        // 1. Validate amount
        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Purchase amount must be greater than zero"
            );
        }


        // 2. Find card
        CreditCard card =
                creditCardRepository
                        .findById(cardId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Credit card not found with id: "
                                                + cardId
                                )
                        );


        // 3. Check card status
        if (!"ACTIVE".equalsIgnoreCase(
                card.getCardStatus())) {

            throw new IllegalArgumentException(
                    "Purchase cannot be made. Card is not active"
            );
        }


        // 4. Check available credit
        if (card.getAvailableCredit()
                .compareTo(amount) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient available credit"
            );
        }


        // 5. Deduct available credit
        card.setAvailableCredit(
                card.getAvailableCredit()
                        .subtract(amount)
        );


        // 6. Increase outstanding
        card.setOutstandingAmount(
                card.getOutstandingAmount()
                        .add(amount)
        );


        // 7. Save
        return creditCardRepository.save(card);
    }


    // =========================================================
    // PROCESS PAYMENT
    // =========================================================
    @Transactional
    public CreditCard processPayment(
            Long cardId,
            BigDecimal amount) {

        // 1. Validate amount
        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }


        // 2. Find card
        CreditCard card =
                creditCardRepository
                        .findById(cardId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Credit card not found with id: "
                                                + cardId
                                )
                        );


        // 3. Check card status
        if (!"ACTIVE".equalsIgnoreCase(
                card.getCardStatus())) {

            throw new IllegalArgumentException(
                    "Payment cannot be made. Card is not active"
            );
        }


        // 4. Check outstanding amount
        if (amount.compareTo(
                card.getOutstandingAmount()) > 0) {

            throw new IllegalArgumentException(
                    "Payment amount cannot exceed outstanding amount"
            );
        }


        // 5. Reduce outstanding
        card.setOutstandingAmount(
                card.getOutstandingAmount()
                        .subtract(amount)
        );


        // 6. Increase available credit
        card.setAvailableCredit(
                card.getAvailableCredit()
                        .add(amount)
        );


        // 7. Save
        return creditCardRepository.save(card);
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private boolean isValidCardType(
            String cardType) {

        return "SILVER".equals(cardType)
                || "GOLD".equals(cardType)
                || "PLATINUM".equals(cardType);
    }


    private String normalizeCardType(
            String cardType) {

        if (cardType == null) {
            return "";
        }

        return cardType.trim().toUpperCase();
    }


    private String normalizeStatus(
            String cardStatus) {

        if (cardStatus == null) {
            return "";
        }

        return cardStatus.trim().toUpperCase();
    }
    
    public BigDecimal getTotalOutstandingAmount() {
        return creditCardRepository.getTotalOutstandingAmount();
    }

    public CreditCard getLowestOutstandingCard() {

        List<CreditCard> cards =
                creditCardRepository.findCardsOrderByOutstandingAsc();

        if (cards.isEmpty()) {
            return null;
        }

        return cards.get(0);
    }
    public CreditCard getHighestOutstandingCard() {

        List<CreditCard> cards =
                creditCardRepository.findCardsOrderByOutstandingDesc();

        if (cards.isEmpty()) {
            return null;
        }

        return cards.get(0);
    }
    public List<CreditCard> getLowAvailableCreditCards() {

        return creditCardRepository
                .findCardsWithLowAvailableCredit();
    }
    public List<CreditCard> getBlockedCards() {

        return creditCardRepository.findBlockedCards();
    }
 // =========================================================
 // CUSTOMER WITH HIGHEST OUTSTANDING
 // =========================================================
 public CustomerOutstandingReport getCustomerWithHighestOutstanding() {

     return creditCardRepository
             .findCustomersByOutstandingDescending()
             .stream()
             .findFirst()
             .orElseThrow(() ->
                     new ResourceNotFoundException(
                             "No credit-card outstanding records found"
                     )
             );
 }


 // =========================================================
 // CUSTOMER WITH LOWEST OUTSTANDING
 // =========================================================
 public CustomerOutstandingReport getCustomerWithLowestOutstanding() {

     return creditCardRepository
             .findCustomersByOutstandingAscending()
             .stream()
             .findFirst()
             .orElseThrow(() ->
                     new ResourceNotFoundException(
                             "No credit-card outstanding records found"
                     )
             );
 }
}







