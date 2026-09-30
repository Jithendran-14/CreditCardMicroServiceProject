package com.ofss.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.jpa.domain.Specification;

import com.ofss.client.CreditCardClient;
import com.ofss.client.MerchantClient;
import com.ofss.dto.CreditCardResponse;
import com.ofss.dto.MerchantResponse;
import com.ofss.dto.PaymentRequest;
import com.ofss.dto.PurchaseRequest;
import com.ofss.entity.Transaction;
import com.ofss.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CreditCardClient creditCardClient;

    @Mock
    private MerchantClient merchantClient;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;
    private MerchantResponse merchant;
    private CreditCardResponse creditCard;

    @BeforeEach
    void setUp() {

        // ---------------------------------------------
        // Sample Transaction
        // ---------------------------------------------

        transaction = new Transaction();

        transaction.setTransactionId(1L);
        transaction.setCardId(2L);
        transaction.setTransactionType("PURCHASE");
        transaction.setAmount(new BigDecimal("10000"));
        transaction.setMerchantId(1L);
        transaction.setTransactionDate(
                LocalDateTime.of(2026, 9, 28, 10, 30)
        );
        transaction.setTransactionStatus("SUCCESS");
        transaction.setFailureReason(null);

        // ---------------------------------------------
        // Sample Merchant
        // ---------------------------------------------

        merchant = new MerchantResponse();

        merchant.setMerchantId(1L);
        merchant.setMerchantName("Amazon");
        merchant.setCategory("E-Commerce");
        merchant.setLocation("Bangalore");

        // ---------------------------------------------
        // Sample Credit Card
        // ---------------------------------------------

        creditCard = new CreditCardResponse();

        creditCard.setCardId(2L);
        creditCard.setCustomerId(1L);
        creditCard.setCardNumber("4222222222222222");
        creditCard.setCardType("SILVER");
        creditCard.setCreditLimit(new BigDecimal("50000"));
        creditCard.setAvailableCredit(new BigDecimal("40000"));
        creditCard.setOutstandingAmount(new BigDecimal("10000"));
        creditCard.setCardStatus("ACTIVE");
    }


    // =========================================================
    // 1. GET ALL TRANSACTIONS
    // =========================================================

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {

        Transaction transaction2 = new Transaction();

        transaction2.setTransactionId(2L);
        transaction2.setCardId(3L);
        transaction2.setTransactionType("PAYMENT");
        transaction2.setAmount(new BigDecimal("5000"));
        transaction2.setTransactionDate(LocalDateTime.now());
        transaction2.setTransactionStatus("SUCCESS");

        when(transactionRepository.findAll())
                .thenReturn(Arrays.asList(transaction, transaction2));

        List<Transaction> result =
                transactionService.getAllTransactions();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L,
                result.get(0).getTransactionId());

        assertEquals(2L,
                result.get(1).getTransactionId());

        verify(transactionRepository, times(1))
                .findAll();
    }


    // =========================================================
    // 2. GET ALL TRANSACTIONS - EMPTY
    // =========================================================

    @Test
    void getAllTransactions_ShouldReturnEmptyList_WhenNoTransactionsExist() {

        when(transactionRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Transaction> result =
                transactionService.getAllTransactions();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepository, times(1))
                .findAll();
    }


    // =========================================================
    // 3. GET TRANSACTION BY ID - SUCCESS
    // =========================================================

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenExists() {

        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));

        Transaction result =
                transactionService.getTransactionById(1L);

        assertNotNull(result);

        assertEquals(1L,
                result.getTransactionId());

        assertEquals("PURCHASE",
                result.getTransactionType());

        assertEquals(
                new BigDecimal("10000"),
                result.getAmount()
        );

        verify(transactionRepository, times(1))
                .findById(1L);
    }


    // =========================================================
    // 4. GET TRANSACTION BY ID - NOT FOUND
    // =========================================================

    @Test
    void getTransactionById_ShouldReturnNull_WhenNotFound() {

        when(transactionRepository.findById(99L))
                .thenReturn(Optional.empty());

        Transaction result =
                transactionService.getTransactionById(99L);

        assertNull(result);

        verify(transactionRepository, times(1))
                .findById(99L);
    }


    // =========================================================
    // 5. PURCHASE - SUCCESS
    // =========================================================

    @Test
    void processPurchase_ShouldCreateSuccessfulTransaction() {

        PurchaseRequest request = new PurchaseRequest();

        request.setCardId(2L);
        request.setMerchantId(1L);
        request.setAmount(new BigDecimal("10000"));

        when(merchantClient.getMerchantById(1L))
                .thenReturn(merchant);

        when(creditCardClient.processPurchase(
                eq(2L),
                eq(new BigDecimal("10000"))
        )).thenReturn(creditCard);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result =
                transactionService.processPurchase(request);

        assertNotNull(result);

        assertEquals(2L,
                result.getCardId());

        assertEquals("PURCHASE",
                result.getTransactionType());

        assertEquals(
                new BigDecimal("10000"),
                result.getAmount()
        );

        assertEquals(1L,
                result.getMerchantId());

        assertEquals("SUCCESS",
                result.getTransactionStatus());

        assertNull(result.getFailureReason());

        assertNotNull(result.getTransactionDate());

        verify(merchantClient, times(1))
                .getMerchantById(1L);

        verify(creditCardClient, times(1))
                .processPurchase(
                        2L,
                        new BigDecimal("10000")
                );

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }


    // =========================================================
    // 6. PURCHASE - MERCHANT NOT FOUND
    // =========================================================

    @Test
    void processPurchase_ShouldCreateFailedTransaction_WhenMerchantNotFound() {

        PurchaseRequest request = new PurchaseRequest();

        request.setCardId(2L);
        request.setMerchantId(99L);
        request.setAmount(new BigDecimal("10000"));

        when(merchantClient.getMerchantById(99L))
                .thenReturn(null);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result =
                transactionService.processPurchase(request);

        assertNotNull(result);

        assertEquals(2L,
                result.getCardId());

        assertEquals("PURCHASE",
                result.getTransactionType());

        assertEquals(
                new BigDecimal("10000"),
                result.getAmount()
        );

        assertEquals(99L,
                result.getMerchantId());

        assertEquals("FAILED",
                result.getTransactionStatus());

        assertEquals(
                "Merchant not found with id: 99",
                result.getFailureReason()
        );

        verify(merchantClient, times(1))
                .getMerchantById(99L);

        verify(creditCardClient, never())
                .processPurchase(anyLong(), any(BigDecimal.class));

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }


    // =========================================================
    // 7. PURCHASE - CREDIT CARD SERVICE RETURNS NULL
    // =========================================================

    @Test
    void processPurchase_ShouldCreateFailedTransaction_WhenCreditCardServiceReturnsNull() {

        PurchaseRequest request = new PurchaseRequest();

        request.setCardId(2L);
        request.setMerchantId(1L);
        request.setAmount(new BigDecimal("10000"));

        when(merchantClient.getMerchantById(1L))
                .thenReturn(merchant);

        when(creditCardClient.processPurchase(
                2L,
                new BigDecimal("10000")
        )).thenReturn(null);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result =
                transactionService.processPurchase(request);

        assertNotNull(result);

        assertEquals("FAILED",
                result.getTransactionStatus());

        assertEquals(
                "Credit Card Management service returned no response",
                result.getFailureReason()
        );

        verify(merchantClient, times(1))
                .getMerchantById(1L);

        verify(creditCardClient, times(1))
                .processPurchase(
                        2L,
                        new BigDecimal("10000")
                );

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }


    // =========================================================
    // 8. PURCHASE - CREDIT CARD SERVICE FAILURE
    // =========================================================

    @Test
    void processPurchase_ShouldCreateFailedTransaction_WhenCreditCardServiceThrowsException() {

        PurchaseRequest request = new PurchaseRequest();

        request.setCardId(2L);
        request.setMerchantId(1L);
        request.setAmount(new BigDecimal("10000"));

        when(merchantClient.getMerchantById(1L))
                .thenReturn(merchant);

        when(creditCardClient.processPurchase(
                2L,
                new BigDecimal("10000")
        )).thenThrow(
                new IllegalArgumentException(
                        "Insufficient available credit"
                )
        );

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result =
                transactionService.processPurchase(request);

        assertNotNull(result);

        assertEquals("FAILED",
                result.getTransactionStatus());

        assertEquals(
                "Insufficient available credit",
                result.getFailureReason()
        );

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }


    // =========================================================
    // 9. PAYMENT - SUCCESS
    // =========================================================

    @Test
    void processPayment_ShouldCreateSuccessfulTransaction() {

        PaymentRequest request = new PaymentRequest();

        request.setCardId(2L);
        request.setAmount(new BigDecimal("5000"));

        when(creditCardClient.processPayment(
                2L,
                new BigDecimal("5000")
        )).thenReturn(creditCard);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result =
                transactionService.processPayment(request);

        assertNotNull(result);

        assertEquals(2L,
                result.getCardId());

        assertEquals("PAYMENT",
                result.getTransactionType());

        assertEquals(
                new BigDecimal("5000"),
                result.getAmount()
        );

        assertNull(result.getMerchantId());

        assertEquals("SUCCESS",
                result.getTransactionStatus());

        assertNull(result.getFailureReason());

        assertNotNull(result.getTransactionDate());

        verify(creditCardClient, times(1))
                .processPayment(
                        2L,
                        new BigDecimal("5000")
                );

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }


    // =========================================================
    // 10. PAYMENT - CREDIT CARD SERVICE RETURNS NULL
    // =========================================================

    @Test
    void processPayment_ShouldCreateFailedTransaction_WhenCreditCardServiceReturnsNull() {

        PaymentRequest request = new PaymentRequest();

        request.setCardId(2L);
        request.setAmount(new BigDecimal("5000"));

        when(creditCardClient.processPayment(
                2L,
                new BigDecimal("5000")
        )).thenReturn(null);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result =
                transactionService.processPayment(request);

        assertNotNull(result);

        assertEquals("PAYMENT",
                result.getTransactionType());

        assertEquals("FAILED",
                result.getTransactionStatus());

        assertEquals(
                "Credit Card Management service returned no response",
                result.getFailureReason()
        );

        assertNull(result.getMerchantId());

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }


    // =========================================================
    // 11. PAYMENT - CREDIT CARD SERVICE FAILURE
    // =========================================================

    @Test
    void processPayment_ShouldCreateFailedTransaction_WhenCreditCardServiceThrowsException() {

        PaymentRequest request = new PaymentRequest();

        request.setCardId(2L);
        request.setAmount(new BigDecimal("5000"));

        when(creditCardClient.processPayment(
                2L,
                new BigDecimal("5000")
        )).thenThrow(
                new IllegalArgumentException(
                        "Payment exceeds outstanding amount"
                )
        );

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result =
                transactionService.processPayment(request);

        assertNotNull(result);

        assertEquals("PAYMENT",
                result.getTransactionType());

        assertEquals("FAILED",
                result.getTransactionStatus());

        assertEquals(
                "Payment exceeds outstanding amount",
                result.getFailureReason()
        );

        assertNull(result.getMerchantId());

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }


    // =========================================================
    // 12. FILTER BY CARD
    // =========================================================

    @Test
    void getTransactionsByCardId_ShouldReturnTransactions() {

        when(transactionRepository.findByCardId(2L))
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.getTransactionsByCardId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                2L,
                result.get(0).getCardId()
        );

        verify(transactionRepository, times(1))
                .findByCardId(2L);
    }


    // =========================================================
    // 13. FILTER BY TYPE
    // =========================================================

    @Test
    void getTransactionsByType_ShouldReturnTransactions() {

        when(transactionRepository.findByTransactionType("PURCHASE"))
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.getTransactionsByType("PURCHASE");

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                "PURCHASE",
                result.get(0).getTransactionType()
        );

        verify(transactionRepository, times(1))
                .findByTransactionType("PURCHASE");
    }


    // =========================================================
    // 14. FILTER BY STATUS
    // =========================================================

    @Test
    void getTransactionsByStatus_ShouldReturnTransactions() {

        when(transactionRepository.findByTransactionStatus("SUCCESS"))
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.getTransactionsByStatus("SUCCESS");

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                "SUCCESS",
                result.get(0).getTransactionStatus()
        );

        verify(transactionRepository, times(1))
                .findByTransactionStatus("SUCCESS");
    }


    // =========================================================
    // 15. FILTER BY MERCHANT
    // =========================================================

    @Test
    void getTransactionsByMerchantId_ShouldReturnTransactions() {

        when(transactionRepository.findByMerchantId(1L))
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.getTransactionsByMerchantId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                1L,
                result.get(0).getMerchantId()
        );

        verify(transactionRepository, times(1))
                .findByMerchantId(1L);
    }


    // =========================================================
    // 16. FILTER BY DATE RANGE
    // =========================================================

    @Test
    void getTransactionsByDateRange_ShouldReturnTransactions() {

        LocalDateTime from =
                LocalDateTime.of(2026, 9, 1, 0, 0);

        LocalDateTime to =
                LocalDateTime.of(2026, 9, 30, 23, 59);

        when(transactionRepository.findByTransactionDateBetween(
                from,
                to
        )).thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.getTransactionsByDateRange(
                        from,
                        to
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findByTransactionDateBetween(from, to);
    }


    // =========================================================
    // 17. SEARCH - NO FILTERS
    // =========================================================

    @Test
    void searchTransactions_ShouldReturnAll_WhenNoFiltersProvided() {

        when(transactionRepository.findAll())
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.searchTransactions(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findAll();

        verify(transactionRepository, never())
                .findAll(any(Specification.class));
    }


    // =========================================================
    // 18. SEARCH - CARD ID
    // =========================================================

    @Test
    void searchTransactions_ShouldSearchByCardId() {

        when(transactionRepository.findAll(
                any(Specification.class)
        )).thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.searchTransactions(
                        2L,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class));
    }


    // =========================================================
    // 19. SEARCH - TYPE
    // =========================================================

    @Test
    void searchTransactions_ShouldSearchByType() {

        when(transactionRepository.findAll(
                any(Specification.class)
        )).thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.searchTransactions(
                        null,
                        "purchase",
                        null,
                        null,
                        null,
                        null
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class));
    }


    // =========================================================
    // 20. SEARCH - STATUS
    // =========================================================

    @Test
    void searchTransactions_ShouldSearchByStatus() {

        when(transactionRepository.findAll(
                any(Specification.class)
        )).thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.searchTransactions(
                        null,
                        null,
                        "success",
                        null,
                        null,
                        null
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class));
    }


    // =========================================================
    // 21. SEARCH - MERCHANT
    // =========================================================

    @Test
    void searchTransactions_ShouldSearchByMerchantId() {

        when(transactionRepository.findAll(
                any(Specification.class)
        )).thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.searchTransactions(
                        null,
                        null,
                        null,
                        1L,
                        null,
                        null
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class));
    }


    // =========================================================
    // 22. SEARCH - FROM DATE
    // =========================================================

    @Test
    void searchTransactions_ShouldSearchFromDate() {

        LocalDateTime from =
                LocalDateTime.of(2026, 9, 1, 0, 0);

        when(transactionRepository.findAll(
                any(Specification.class)
        )).thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.searchTransactions(
                        null,
                        null,
                        null,
                        null,
                        from,
                        null
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class));
    }


    // =========================================================
    // 23. SEARCH - TO DATE
    // =========================================================

    @Test
    void searchTransactions_ShouldSearchToDate() {

        LocalDateTime to =
                LocalDateTime.of(2026, 9, 30, 23, 59);

        when(transactionRepository.findAll(
                any(Specification.class)
        )).thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.searchTransactions(
                        null,
                        null,
                        null,
                        null,
                        null,
                        to
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class));
    }


    // =========================================================
    // 24. SEARCH - MULTIPLE FILTERS
    // =========================================================

    @Test
    void searchTransactions_ShouldSearchUsingMultipleFilters() {

        LocalDateTime from =
                LocalDateTime.of(2026, 9, 1, 0, 0);

        LocalDateTime to =
                LocalDateTime.of(2026, 9, 30, 23, 59);

        when(transactionRepository.findAll(
                any(Specification.class)
        )).thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.searchTransactions(
                        2L,
                        "purchase",
                        "success",
                        1L,
                        from,
                        to
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(transactionRepository, times(1))
                .findAll(any(Specification.class));
    }
}