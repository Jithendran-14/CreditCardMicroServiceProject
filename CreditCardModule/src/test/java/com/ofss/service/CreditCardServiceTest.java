package com.ofss.service;

import com.ofss.client.CustomerClient;
import com.ofss.dto.CustomerResponse;
import com.ofss.entity.CreditCard;
import com.ofss.exception.ResourceNotFoundException;
import com.ofss.repository.CreditCardRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private CreditCardService creditCardService;

    private CreditCard card;

    @BeforeEach
    void setUp() {

        card = new CreditCard();

        card.setCardId(1L);
        card.setCustomerId(1L);
        card.setCardNumber("4222222222222222");
        card.setCardType("SILVER");
        card.setCreditLimit(new BigDecimal("50000"));
        card.setAvailableCredit(new BigDecimal("50000"));
        card.setOutstandingAmount(new BigDecimal("0"));
        card.setExpiryDate(LocalDate.now().plusYears(2));
        card.setCardStatus("ACTIVE");
    }


    // =========================================================
    // 1. ISSUE CARD - SUCCESS
    // =========================================================

    @Test
    void issueCard_shouldCreateCardSuccessfully() {

        CustomerResponse customer = new CustomerResponse();

        when(customerClient.getCustomerById(1L))
                .thenReturn(customer);

        when(creditCardRepository.existsByCardNumber(
                "4222222222222222"))
                .thenReturn(false);

        when(creditCardRepository.save(card))
                .thenReturn(card);

        CreditCard result =
                creditCardService.issueCard(card);

        assertNotNull(result);
        assertEquals("SILVER", result.getCardType());
        assertEquals(
                new BigDecimal("50000"),
                result.getAvailableCredit()
        );
        assertEquals(
                new BigDecimal("0"),
                result.getOutstandingAmount()
        );
        assertEquals("ACTIVE", result.getCardStatus());

        verify(customerClient, times(1))
                .getCustomerById(1L);

        verify(creditCardRepository, times(1))
                .existsByCardNumber("4222222222222222");

        verify(creditCardRepository, times(1))
                .save(card);
    }


    // =========================================================
    // 2. ISSUE CARD - CUSTOMER ID MISSING
    // =========================================================

    @Test
    void issueCard_shouldThrowException_whenCustomerIdIsMissing() {

        card.setCustomerId(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.issueCard(card)
                );

        assertEquals(
                "Customer ID is required",
                exception.getMessage()
        );

        verifyNoInteractions(customerClient);
        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 3. ISSUE CARD - CUSTOMER NOT FOUND
    // =========================================================

    @Test
    void issueCard_shouldThrowException_whenCustomerDoesNotExist() {

        when(customerClient.getCustomerById(1L))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> creditCardService.issueCard(card)
                );

        assertEquals(
                "Customer not found with id: 1",
                exception.getMessage()
        );

        verify(customerClient, times(1))
                .getCustomerById(1L);

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 4. ISSUE CARD - DUPLICATE CARD NUMBER
    // =========================================================

    @Test
    void issueCard_shouldThrowException_whenCardNumberAlreadyExists() {

        when(customerClient.getCustomerById(1L))
                .thenReturn(new CustomerResponse());

        when(creditCardRepository.existsByCardNumber(
                "4222222222222222"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.issueCard(card)
                );

        assertEquals(
                "Card number already exists",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 5. ISSUE CARD - INVALID CARD TYPE
    // =========================================================

    @Test
    void issueCard_shouldThrowException_whenCardTypeIsInvalid() {

        card.setCardType("DIAMOND");

        when(customerClient.getCustomerById(1L))
                .thenReturn(new CustomerResponse());

        when(creditCardRepository.existsByCardNumber(
                "4222222222222222"))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.issueCard(card)
                );

        assertEquals(
                "Card type must be SILVER, GOLD or PLATINUM",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 6. ISSUE CARD - INVALID CREDIT LIMIT
    // =========================================================

    @Test
    void issueCard_shouldThrowException_whenCreditLimitIsInvalid() {

        card.setCreditLimit(BigDecimal.ZERO);

        when(customerClient.getCustomerById(1L))
                .thenReturn(new CustomerResponse());

        when(creditCardRepository.existsByCardNumber(
                "4222222222222222"))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.issueCard(card)
                );

        assertEquals(
                "Credit limit must be greater than zero",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 7. ISSUE CARD - EXPIRED CARD
    // =========================================================

    @Test
    void issueCard_shouldThrowException_whenExpiryDateIsInvalid() {

        card.setExpiryDate(LocalDate.now().minusDays(1));

        when(customerClient.getCustomerById(1L))
                .thenReturn(new CustomerResponse());

        when(creditCardRepository.existsByCardNumber(
                "4222222222222222"))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.issueCard(card)
                );

        assertEquals(
                "Expiry date must be in the future",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 8. GET ALL CARDS
    // =========================================================

    @Test
    void getAllCards_shouldReturnAllCards() {

        CreditCard card2 = new CreditCard();
        card2.setCardId(2L);
        card2.setCustomerId(2L);
        card2.setCardNumber("4333333333333333");

        List<CreditCard> cards =
                Arrays.asList(card, card2);

        when(creditCardRepository.findAll())
                .thenReturn(cards);

        List<CreditCard> result =
                creditCardService.getAllCards();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getCardId());
        assertEquals(2L, result.get(1).getCardId());

        verify(creditCardRepository, times(1))
                .findAll();
    }


    // =========================================================
    // 9. GET CARD BY ID - SUCCESS
    // =========================================================

    @Test
    void getCardById_shouldReturnCard_whenCardExists() {

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        CreditCard result =
                creditCardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCardId());
        assertEquals(
                "4222222222222222",
                result.getCardNumber()
        );

        verify(creditCardRepository, times(1))
                .findById(1L);
    }


    // =========================================================
    // 10. GET CARD BY ID - NOT FOUND
    // =========================================================

    @Test
    void getCardById_shouldThrowException_whenCardDoesNotExist() {

        when(creditCardRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> creditCardService.getCardById(999L)
                );

        assertEquals(
                "Credit card not found with id: 999",
                exception.getMessage()
        );
    }


    // =========================================================
    // 11. GET CARDS BY CUSTOMER
    // =========================================================

    @Test
    void getCardsByCustomerId_shouldReturnCustomerCards() {

        when(creditCardRepository.findByCustomerId(1L))
                .thenReturn(Collections.singletonList(card));

        List<CreditCard> result =
                creditCardService.getCardsByCustomerId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCustomerId());

        verify(creditCardRepository, times(1))
                .findByCustomerId(1L);
    }


    // =========================================================
    // 12. UPDATE CARD - SUCCESS
    // =========================================================

    @Test
    void updateCard_shouldUpdateCardSuccessfully() {

        CreditCard updatedCard = new CreditCard();

        updatedCard.setCardType("GOLD");
        updatedCard.setExpiryDate(
                LocalDate.now().plusYears(3)
        );

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        when(creditCardRepository.save(card))
                .thenReturn(card);

        CreditCard result =
                creditCardService.updateCard(
                        1L,
                        updatedCard
                );

        assertNotNull(result);
        assertEquals("GOLD", result.getCardType());
        assertEquals(
                LocalDate.now().plusYears(3),
                result.getExpiryDate()
        );

        verify(creditCardRepository, times(1))
                .findById(1L);

        verify(creditCardRepository, times(1))
                .save(card);
    }


    // =========================================================
    // 13. UPDATE CARD - NOT FOUND
    // =========================================================

    @Test
    void updateCard_shouldThrowException_whenCardDoesNotExist() {

        when(creditCardRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> creditCardService.updateCard(
                                999L,
                                new CreditCard()
                        )
                );

        assertEquals(
                "Credit card not found with id: 999",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 14. UPDATE CARD - INVALID TYPE
    // =========================================================

    @Test
    void updateCard_shouldThrowException_whenCardTypeIsInvalid() {

        CreditCard updatedCard = new CreditCard();
        updatedCard.setCardType("DIAMOND");

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.updateCard(
                                1L,
                                updatedCard
                        )
                );

        assertEquals(
                "Card type must be SILVER, GOLD or PLATINUM",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 15. BLOCK CARD
    // =========================================================

    @Test
    void updateCardStatus_shouldBlockCardSuccessfully() {

        CreditCard updatedCard = new CreditCard();
        updatedCard.setCardStatus("BLOCKED");

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        when(creditCardRepository.save(card))
                .thenReturn(card);

        CreditCard result =
                creditCardService.updateCardStatus(
                        1L,
                        updatedCard
                );

        assertEquals("BLOCKED", result.getCardStatus());

        verify(creditCardRepository, times(1))
                .save(card);
    }


    // =========================================================
    // 16. INVALID CARD STATUS
    // =========================================================

    @Test
    void updateCardStatus_shouldThrowException_whenStatusIsInvalid() {

        CreditCard updatedCard = new CreditCard();
        updatedCard.setCardStatus("CANCELLED");

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.updateCardStatus(
                                1L,
                                updatedCard
                        )
                );

        assertEquals(
                "Card status must be ACTIVE or BLOCKED",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 17. PURCHASE - SUCCESS
    // =========================================================

    @Test
    void processPurchase_shouldUpdateCreditSuccessfully() {

        card.setAvailableCredit(new BigDecimal("50000"));
        card.setOutstandingAmount(new BigDecimal("0"));

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        when(creditCardRepository.save(card))
                .thenReturn(card);

        CreditCard result =
                creditCardService.processPurchase(
                        1L,
                        new BigDecimal("10000")
                );

        assertEquals(
                new BigDecimal("40000"),
                result.getAvailableCredit()
        );

        assertEquals(
                new BigDecimal("10000"),
                result.getOutstandingAmount()
        );

        verify(creditCardRepository, times(1))
                .save(card);
    }


    // =========================================================
    // 18. PURCHASE - INVALID AMOUNT
    // =========================================================

    @Test
    void processPurchase_shouldThrowException_whenAmountIsInvalid() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.processPurchase(
                                1L,
                                BigDecimal.ZERO
                        )
                );

        assertEquals(
                "Purchase amount must be greater than zero",
                exception.getMessage()
        );

        verifyNoInteractions(creditCardRepository);
    }


    // =========================================================
    // 19. PURCHASE - BLOCKED CARD
    // =========================================================

    @Test
    void processPurchase_shouldThrowException_whenCardIsBlocked() {

        card.setCardStatus("BLOCKED");

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.processPurchase(
                                1L,
                                new BigDecimal("5000")
                        )
                );

        assertEquals(
                "Purchase cannot be made. Card is not active",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 20. PURCHASE - INSUFFICIENT CREDIT
    // =========================================================

    @Test
    void processPurchase_shouldThrowException_whenCreditIsInsufficient() {

        card.setAvailableCredit(new BigDecimal("5000"));

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.processPurchase(
                                1L,
                                new BigDecimal("10000")
                        )
                );

        assertEquals(
                "Insufficient available credit",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 21. PAYMENT - SUCCESS
    // =========================================================

    @Test
    void processPayment_shouldUpdateCreditSuccessfully() {

        card.setAvailableCredit(new BigDecimal("25000"));
        card.setOutstandingAmount(new BigDecimal("25000"));

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        when(creditCardRepository.save(card))
                .thenReturn(card);

        CreditCard result =
                creditCardService.processPayment(
                        1L,
                        new BigDecimal("5000")
                );

        assertEquals(
                new BigDecimal("30000"),
                result.getAvailableCredit()
        );

        assertEquals(
                new BigDecimal("20000"),
                result.getOutstandingAmount()
        );

        verify(creditCardRepository, times(1))
                .save(card);
    }


    // =========================================================
    // 22. PAYMENT - INVALID AMOUNT
    // =========================================================

    @Test
    void processPayment_shouldThrowException_whenAmountIsInvalid() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.processPayment(
                                1L,
                                BigDecimal.ZERO
                        )
                );

        assertEquals(
                "Payment amount must be greater than zero",
                exception.getMessage()
        );

        verifyNoInteractions(creditCardRepository);
    }


    // =========================================================
    // 23. PAYMENT - BLOCKED CARD
    // =========================================================

    @Test
    void processPayment_shouldThrowException_whenCardIsBlocked() {

        card.setCardStatus("BLOCKED");

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.processPayment(
                                1L,
                                new BigDecimal("5000")
                        )
                );

        assertEquals(
                "Payment cannot be made. Card is not active",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 24. PAYMENT - GREATER THAN OUTSTANDING
    // =========================================================

    @Test
    void processPayment_shouldThrowException_whenPaymentExceedsOutstanding() {

        card.setOutstandingAmount(new BigDecimal("5000"));

        when(creditCardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creditCardService.processPayment(
                                1L,
                                new BigDecimal("10000")
                        )
                );

        assertEquals(
                "Payment amount cannot exceed outstanding amount",
                exception.getMessage()
        );

        verify(creditCardRepository, never())
                .save(any(CreditCard.class));
    }


    // =========================================================
    // 25. TOTAL OUTSTANDING REPORT
    // =========================================================

    @Test
    void getTotalOutstandingAmount_shouldReturnTotal() {

        BigDecimal total =
                new BigDecimal("75000");

        when(creditCardRepository.getTotalOutstandingAmount())
                .thenReturn(total);

        BigDecimal result =
                creditCardService.getTotalOutstandingAmount();

        assertEquals(total, result);

        verify(creditCardRepository, times(1))
                .getTotalOutstandingAmount();
    }


    // =========================================================
    // 26. LOWEST OUTSTANDING CARD
    // =========================================================

    @Test
    void getLowestOutstandingCard_shouldReturnLowestCard() {

        CreditCard lowest = new CreditCard();
        lowest.setCardId(2L);
        lowest.setOutstandingAmount(
                new BigDecimal("5000")
        );

        when(creditCardRepository.findCardsOrderByOutstandingAsc())
                .thenReturn(Arrays.asList(lowest, card));

        CreditCard result =
                creditCardService.getLowestOutstandingCard();

        assertNotNull(result);
        assertEquals(2L, result.getCardId());

        verify(creditCardRepository, times(1))
                .findCardsOrderByOutstandingAsc();
    }


    // =========================================================
    // 27. HIGHEST OUTSTANDING CARD
    // =========================================================

    @Test
    void getHighestOutstandingCard_shouldReturnHighestCard() {

        CreditCard highest = new CreditCard();
        highest.setCardId(3L);
        highest.setOutstandingAmount(
                new BigDecimal("90000")
        );

        when(creditCardRepository.findCardsOrderByOutstandingDesc())
                .thenReturn(Arrays.asList(highest, card));

        CreditCard result =
                creditCardService.getHighestOutstandingCard();

        assertNotNull(result);
        assertEquals(3L, result.getCardId());

        verify(creditCardRepository, times(1))
                .findCardsOrderByOutstandingDesc();
    }


    // =========================================================
    // 28. LOW AVAILABLE CREDIT REPORT
    // =========================================================

    @Test
    void getLowAvailableCreditCards_shouldReturnCards() {

        when(creditCardRepository.findCardsWithLowAvailableCredit())
                .thenReturn(Collections.singletonList(card));

        List<CreditCard> result =
                creditCardService.getLowAvailableCreditCards();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCardId());

        verify(creditCardRepository, times(1))
                .findCardsWithLowAvailableCredit();
    }


    // =========================================================
    // 29. BLOCKED CARD REPORT
    // =========================================================

    @Test
    void getBlockedCards_shouldReturnBlockedCards() {

        card.setCardStatus("BLOCKED");

        when(creditCardRepository.findBlockedCards())
                .thenReturn(Collections.singletonList(card));

        List<CreditCard> result =
                creditCardService.getBlockedCards();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BLOCKED",
                result.get(0).getCardStatus());

        verify(creditCardRepository, times(1))
                .findBlockedCards();
    }
}