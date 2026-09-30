package com.ofss.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ofss.client.CustomerClient;
import com.ofss.dto.CustomerOutstandingReport;
import com.ofss.dto.CustomerResponse;
import com.ofss.entity.CreditCard;
import com.ofss.exception.ResourceNotFoundException;
import com.ofss.repository.CreditCardRepository;

import jakarta.transaction.Transactional;

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

    public CreditCard issueCard(CreditCard card) {

        if (card.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        CustomerResponse customer =
                customerClient.getCustomerById(card.getCustomerId());

        if (customer == null) {
            throw new ResourceNotFoundException(
                    "Customer not found with id: " + card.getCustomerId()
            );
        }

        if (card.getCardNumber() == null ||
                card.getCardNumber().isBlank()) {
            throw new IllegalArgumentException("Card number is required");
        }

        if (creditCardRepository.existsByCardNumber(card.getCardNumber())) {
            throw new IllegalArgumentException("Card number already exists");
        }

        String cardType = normalizeCardType(card.getCardType());

        if (!isValidCardType(cardType)) {
            throw new IllegalArgumentException(
                    "Card type must be SILVER, GOLD or PLATINUM"
            );
        }

        if (card.getCreditLimit() == null ||
                card.getCreditLimit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Credit limit must be greater than zero"
            );
        }

        if (card.getExpiryDate() == null ||
                card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Expiry date must be in the future"
            );
        }

        card.setCardType(cardType);
        card.setAvailableCredit(card.getCreditLimit());
        card.setOutstandingAmount(BigDecimal.ZERO);
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

    public List<CreditCard> getAllCards() {
        return creditCardRepository.findAll();
    }

    public CreditCard getCardById(Long cardId) {
        return creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credit card not found with id: " + cardId
                ));
    }

    public List<CreditCard> getCardsByCustomerId(Long customerId) {
        return creditCardRepository.findByCustomerId(customerId);
    }

    public CreditCard updateCard(Long cardId, CreditCard updatedCard) {

        CreditCard existingCard = getCardById(cardId);

        if (updatedCard.getCardType() != null) {

            String cardType = normalizeCardType(updatedCard.getCardType());

            if (!isValidCardType(cardType)) {
                throw new IllegalArgumentException(
                        "Card type must be SILVER, GOLD or PLATINUM"
                );
            }

            existingCard.setCardType(cardType);
        }

        if (updatedCard.getExpiryDate() != null) {

            if (updatedCard.getExpiryDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Expiry date must be in the future"
                );
            }

            existingCard.setExpiryDate(updatedCard.getExpiryDate());
        }

        return creditCardRepository.save(existingCard);
    }

    public CreditCard updateCardStatus(
            Long cardId,
            CreditCard updatedCard) {

        CreditCard existingCard = getCardById(cardId);

        String status = normalizeStatus(updatedCard.getCardStatus());

        if (!"ACTIVE".equals(status) && !"BLOCKED".equals(status)) {
            throw new IllegalArgumentException(
                    "Card status must be ACTIVE or BLOCKED"
            );
        }

        existingCard.setCardStatus(status);

        return creditCardRepository.save(existingCard);
    }

    @Transactional
    public CreditCard processPurchase(Long cardId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Purchase amount must be greater than zero"
            );
        }

        CreditCard card = getCardById(cardId);

        if (!"ACTIVE".equalsIgnoreCase(card.getCardStatus())) {
            throw new IllegalArgumentException(
                    "Purchase cannot be made. Card is not active"
            );
        }

        if (card.getAvailableCredit().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient available credit");
        }

        card.setAvailableCredit(card.getAvailableCredit().subtract(amount));
        card.setOutstandingAmount(card.getOutstandingAmount().add(amount));

        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard processPayment(Long cardId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        CreditCard card = getCardById(cardId);

        if (!"ACTIVE".equalsIgnoreCase(card.getCardStatus())) {
            throw new IllegalArgumentException(
                    "Payment cannot be made. Card is not active"
            );
        }

        if (amount.compareTo(card.getOutstandingAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Payment amount cannot exceed outstanding amount"
            );
        }

        card.setOutstandingAmount(
                card.getOutstandingAmount().subtract(amount)
        );

        card.setAvailableCredit(
                card.getAvailableCredit().add(amount)
        );

        return creditCardRepository.save(card);
    }

    public BigDecimal getTotalOutstandingAmount() {
        return creditCardRepository.getTotalOutstandingAmount();
    }

    public CreditCard getLowestOutstandingCard() {

        List<CreditCard> cards =
                creditCardRepository.findCardsOrderByOutstandingAsc();

        return cards.isEmpty() ? null : cards.get(0);
    }

    public CreditCard getHighestOutstandingCard() {

        List<CreditCard> cards =
                creditCardRepository.findCardsOrderByOutstandingDesc();

        return cards.isEmpty() ? null : cards.get(0);
    }

    public List<CreditCard> getLowAvailableCreditCards() {
        return creditCardRepository.findCardsWithLowAvailableCredit();
    }

    public List<CreditCard> getBlockedCards() {
        return creditCardRepository.findBlockedCards();
    }

    public List<CustomerOutstandingReport>
            getCustomersWithHighestOutstanding() {

        List<CustomerOutstandingReport> reports =
                creditCardRepository
                        .findCustomersByOutstandingDescending();

        if (reports.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No credit-card outstanding records found"
            );
        }

        BigDecimal highestOutstanding =
                reports.get(0).getTotalOutstandingAmount();

        return reports.stream()
                .filter(report ->
                        report.getTotalOutstandingAmount()
                                .compareTo(highestOutstanding) == 0
                )
                .toList();
    }

    public List<CustomerOutstandingReport>
            getCustomersWithLowestOutstanding() {

        List<CustomerOutstandingReport> reports =
                creditCardRepository
                        .findCustomersByOutstandingAscending();

        if (reports.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No credit-card outstanding records found"
            );
        }

        BigDecimal lowestOutstanding =
                reports.get(0).getTotalOutstandingAmount();

        return reports.stream()
                .filter(report ->
                        report.getTotalOutstandingAmount()
                                .compareTo(lowestOutstanding) == 0
                )
                .toList();
    }

    private boolean isValidCardType(String cardType) {
        return "SILVER".equals(cardType)
                || "GOLD".equals(cardType)
                || "PLATINUM".equals(cardType);
    }

    private String normalizeCardType(String cardType) {
        return cardType == null ? "" : cardType.trim().toUpperCase();
    }

    private String normalizeStatus(String cardStatus) {
        return cardStatus == null ? "" : cardStatus.trim().toUpperCase();
    }
    
    public List<CreditCard> getCardsWithHighestAvailableCredit() {

        List<CreditCard> cards =
                creditCardRepository.findCardsOrderByAvailableCreditDesc();

        if (cards.isEmpty()) {
            throw new ResourceNotFoundException("No credit cards found");
        }

        BigDecimal highestAvailableCredit =
                cards.get(0).getAvailableCredit();

        return cards.stream()
                .filter(card ->
                        card.getAvailableCredit()
                                .compareTo(highestAvailableCredit) == 0
                )
                .toList();
    }

    public List<CreditCard> getCardsWithLowestAvailableCredit() {

        List<CreditCard> cards =
                creditCardRepository.findCardsOrderByAvailableCreditAsc();

        if (cards.isEmpty()) {
            throw new ResourceNotFoundException("No credit cards found");
        }

        BigDecimal lowestAvailableCredit =
                cards.get(0).getAvailableCredit();

        return cards.stream()
                .filter(card ->
                        card.getAvailableCredit()
                                .compareTo(lowestAvailableCredit) == 0
                )
                .toList();
    }
}