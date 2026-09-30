package com.ofss.service;

import com.ofss.client.CreditCardClient;
import com.ofss.client.MerchantClient;
import com.ofss.dto.CreditCardResponse;
import com.ofss.dto.PaymentRequest;
import com.ofss.dto.PurchaseRequest;
import com.ofss.dto.MerchantResponse;
import com.ofss.entity.Transaction;
import com.ofss.exception.ConcurrentTransactionException;
import com.ofss.repository.TransactionRepository;
import com.ofss.specification.TransactionSpecification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.ofss.dto.CreditCardStatement;

import java.math.RoundingMode;
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CreditCardClient creditCardClient;
    private final MerchantClient merchantClient;

    public TransactionService(
            TransactionRepository transactionRepository,
            CreditCardClient creditCardClient,
            MerchantClient merchantClient) {

        this.transactionRepository = transactionRepository;
        this.creditCardClient = creditCardClient;
        this.merchantClient = merchantClient;
    }

    // =========================================================
    // GET ALL TRANSACTIONS
    // =========================================================

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // =========================================================
    // GET TRANSACTION BY ID
    // =========================================================

    public Transaction getTransactionById(Long transactionId) {

        return transactionRepository.findById(transactionId)
                .orElse(null);
    }

    // =========================================================
    // PURCHASE
    // =========================================================

    public Transaction processPurchase(PurchaseRequest request) {

        MerchantResponse merchant =
                merchantClient.getMerchantById(request.getMerchantId());

        if (merchant == null) {
            return saveFailedPurchase(
                    request,
                    "Merchant not found with id: "
                            + request.getMerchantId()
            );
        }

        try {

            CreditCardResponse updatedCard =
                    creditCardClient.processPurchase(
                            request.getCardId(),
                            request.getAmount()
                    );

            if (updatedCard == null) {
                return saveFailedPurchase(
                        request,
                        "Credit Card Management service returned no response"
                );
            }

            Transaction transaction = new Transaction();

            transaction.setCardId(request.getCardId());
            transaction.setCustomerId(updatedCard.getCustomerId());
            transaction.setTransactionType("PURCHASE");
            transaction.setAmount(request.getAmount());
            transaction.setMerchantId(request.getMerchantId());
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setTransactionStatus("SUCCESS");
            transaction.setFailureReason(null);

            return transactionRepository.save(transaction);

        }  catch (ConcurrentTransactionException exception) {

            return saveFailedPurchase(
                    request,
                    exception.getMessage()
            );

        } catch (IllegalArgumentException exception) {

            return saveFailedPurchase(
                    request,
                    exception.getMessage()
            );
        }
    }

    // =========================================================
    // PAYMENT
    // =========================================================
    public Transaction processPayment(PaymentRequest request) {

        try {

            CreditCardResponse updatedCard =
                    creditCardClient.processPayment(
                            request.getCardId(),
                            request.getAmount()
                    );

            if (updatedCard == null) {
                return saveFailedPayment(
                        request,
                        "Credit Card Management service returned no response"
                );
            }

            Transaction transaction = new Transaction();

            transaction.setCardId(request.getCardId());
            transaction.setCustomerId(updatedCard.getCustomerId());
            transaction.setTransactionType("PAYMENT");
            transaction.setAmount(request.getAmount());
            transaction.setMerchantId(null);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setTransactionStatus("SUCCESS");
            transaction.setFailureReason(null);

            return transactionRepository.save(transaction);

        } catch (ConcurrentTransactionException exception) {

            return saveFailedPayment(
                    request,
                    exception.getMessage()
            );

        } catch (IllegalArgumentException exception) {

            return saveFailedPayment(
                    request,
                    exception.getMessage()
            );
        }
    }
    // =========================================================
    // FAILED PURCHASE
    // =========================================================

    private Transaction saveFailedPurchase(
            PurchaseRequest request,
            String failureReason) {

        Transaction transaction = new Transaction();

        transaction.setCardId(request.getCardId());
        transaction.setTransactionType("PURCHASE");
        transaction.setAmount(request.getAmount());
        transaction.setMerchantId(request.getMerchantId());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionStatus("FAILED");
        transaction.setFailureReason(failureReason);

        return transactionRepository.save(transaction);
    }

    // =========================================================
    // FAILED PAYMENT
    // =========================================================

    private Transaction saveFailedPayment(
            PaymentRequest request,
            String failureReason) {

        Transaction transaction = new Transaction();

        transaction.setCardId(request.getCardId());
        transaction.setTransactionType("PAYMENT");
        transaction.setAmount(request.getAmount());
        transaction.setMerchantId(null);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionStatus("FAILED");
        transaction.setFailureReason(failureReason);

        return transactionRepository.save(transaction);
    }

    // =========================================================
    // FILTER - BY CARD
    // =========================================================

    public List<Transaction> getTransactionsByCardId(Long cardId) {

        return transactionRepository.findByCardId(cardId);
    }

    // =========================================================
    // FILTER - BY TYPE
    // =========================================================

    public List<Transaction> getTransactionsByType(
            String transactionType) {

        return transactionRepository.findByTransactionType(
                transactionType
        );
    }

    // =========================================================
    // FILTER - BY STATUS
    // =========================================================

    public List<Transaction> getTransactionsByStatus(
            String transactionStatus) {

        return transactionRepository.findByTransactionStatus(
                transactionStatus
        );
    }

    // =========================================================
    // FILTER - BY MERCHANT
    // =========================================================

    public List<Transaction> getTransactionsByMerchantId(
            Long merchantId) {

        return transactionRepository.findByMerchantId(
                merchantId
        );
    }

    // =========================================================
    // FILTER - BY DATE RANGE
    // =========================================================

    public List<Transaction> getTransactionsByDateRange(
            LocalDateTime from,
            LocalDateTime to) {

        return transactionRepository.findByTransactionDateBetween(
                from,
                to
        );
    }

    // =========================================================
    // COMBINED TRANSACTION SEARCH
    // =========================================================

    public List<Transaction> searchTransactions(
            Long cardId,
            String type,
            String status,
            Long merchantId,
            LocalDateTime from,
            LocalDateTime to) {

        /*
         * Start with no specification.
         *
         * Each filter is added only when the user
         * actually provides that parameter.
         */
        Specification<Transaction> specification = null;

        // -----------------------------------------------------
        // CARD ID
        // -----------------------------------------------------

        if (cardId != null) {

            specification = Specification.where(
                    TransactionSpecification.hasCardId(cardId)
            );
        }

        // -----------------------------------------------------
        // TRANSACTION TYPE
        // -----------------------------------------------------

        if (type != null && !type.isBlank()) {

            Specification<Transaction> typeSpecification =
                    TransactionSpecification.hasTransactionType(
                            type.toUpperCase()
                    );

            specification = specification == null
                    ? typeSpecification
                    : specification.and(typeSpecification);
        }

        // -----------------------------------------------------
        // TRANSACTION STATUS
        // -----------------------------------------------------

        if (status != null && !status.isBlank()) {

            Specification<Transaction> statusSpecification =
                    TransactionSpecification.hasTransactionStatus(
                            status.toUpperCase()
                    );

            specification = specification == null
                    ? statusSpecification
                    : specification.and(statusSpecification);
        }

        // -----------------------------------------------------
        // MERCHANT ID
        // -----------------------------------------------------

        if (merchantId != null) {

            Specification<Transaction> merchantSpecification =
                    TransactionSpecification.hasMerchantId(
                            merchantId
                    );

            specification = specification == null
                    ? merchantSpecification
                    : specification.and(merchantSpecification);
        }

        // -----------------------------------------------------
        // FROM DATE
        // -----------------------------------------------------

        if (from != null) {

            Specification<Transaction> fromSpecification =
                    TransactionSpecification.dateGreaterThanOrEqual(
                            from
                    );

            specification = specification == null
                    ? fromSpecification
                    : specification.and(fromSpecification);
        }

        // -----------------------------------------------------
        // TO DATE
        // -----------------------------------------------------

        if (to != null) {

            Specification<Transaction> toSpecification =
                    TransactionSpecification.dateLessThanOrEqual(
                            to
                    );

            specification = specification == null
                    ? toSpecification
                    : specification.and(toSpecification);
        }

        // -----------------------------------------------------
        // EXECUTE SEARCH
        // -----------------------------------------------------

        if (specification == null) {

            return transactionRepository.findAll();
        }

        return transactionRepository.findAll(specification);
    }
    
    // Filter by amount
    public List<Transaction> getTransactionsByAmount(
            BigDecimal amount) {

        return transactionRepository.findByAmount(amount);
    }
    
    // Credit Card Statement
    public CreditCardStatement generateStatement(
            Long cardId,
            LocalDateTime from,
            LocalDateTime to) {

        List<Transaction> transactions;

        if (from != null && to != null) {

            transactions =
                    transactionRepository
                            .findByCardIdAndTransactionDateBetweenOrderByTransactionDateAsc(
                                    cardId,
                                    from,
                                    to
                            );

        } else {

            transactions =
                    transactionRepository
                            .findByCardId(cardId)
                            .stream()
                            .sorted(
                                    java.util.Comparator.comparing(
                                            Transaction::getTransactionDate
                                    )
                            )
                            .toList();
        }

        BigDecimal totalPurchaseAmount =
                transactions.stream()
                        .filter(t ->
                                "PURCHASE".equals(
                                        t.getTransactionType()
                                )
                        )
                        .filter(t ->
                                "SUCCESS".equals(
                                        t.getTransactionStatus()
                                )
                        )
                        .map(Transaction::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalPaymentAmount =
                transactions.stream()
                        .filter(t ->
                                "PAYMENT".equals(
                                        t.getTransactionType()
                                )
                        )
                        .filter(t ->
                                "SUCCESS".equals(
                                        t.getTransactionStatus()
                                )
                        )
                        .map(Transaction::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal netAmount =
                totalPurchaseAmount
                        .subtract(totalPaymentAmount);

        long purchaseCount =
                transactions.stream()
                        .filter(t ->
                                "PURCHASE".equals(
                                        t.getTransactionType()
                                )
                        )
                        .filter(t ->
                                "SUCCESS".equals(
                                        t.getTransactionStatus()
                                )
                        )
                        .count();

        long paymentCount =
                transactions.stream()
                        .filter(t ->
                                "PAYMENT".equals(
                                        t.getTransactionType()
                                )
                        )
                        .filter(t ->
                                "SUCCESS".equals(
                                        t.getTransactionStatus()
                                )
                        )
                        .count();

        CreditCardStatement statement =
                new CreditCardStatement();

        statement.setCardId(cardId);
        statement.setStatementFrom(from);
        statement.setStatementTo(to);
        statement.setTotalPurchaseAmount(
                totalPurchaseAmount
        );
        statement.setTotalPaymentAmount(
                totalPaymentAmount
        );
        statement.setNetAmount(netAmount);
        statement.setPurchaseCount(purchaseCount);
        statement.setPaymentCount(paymentCount);
        statement.setTransactions(transactions);

        return statement;
    }
}



