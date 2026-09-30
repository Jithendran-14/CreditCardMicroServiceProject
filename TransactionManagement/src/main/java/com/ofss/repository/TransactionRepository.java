package com.ofss.repository;

import com.ofss.entity.Transaction;

import com.ofss.dto.report.CardSpendingReport;
import com.ofss.dto.report.CardUsageReport;
import com.ofss.dto.report.CustomerMonthlySpendingReport;
import com.ofss.dto.report.CustomerSpendingReport;
import com.ofss.dto.report.DailyTransactionReport;
import com.ofss.dto.report.MerchantSalesReport;
import com.ofss.dto.report.MerchantTransactionCountReport;
import com.ofss.dto.report.MonthlySpendingReport;
import com.ofss.dto.report.TransactionTypeSummaryReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
                JpaSpecificationExecutor<Transaction> {

    // =========================================================
    // EXISTING FILTERS
    // =========================================================

    List<Transaction> findByCardId(Long cardId);

    List<Transaction> findByTransactionType(String transactionType);

    List<Transaction> findByTransactionStatus(String transactionStatus);

    List<Transaction> findByMerchantId(Long merchantId);

    List<Transaction> findByTransactionDateBetween(
            LocalDateTime from,
            LocalDateTime to
    );

    // =========================================================
    // SEARCH TRANSACTIONS BY AMOUNT
    // =========================================================

    List<Transaction> findByAmount(BigDecimal amount);

    // =========================================================
    // CARD STATEMENT - CARD + DATE RANGE
    // =========================================================

    List<Transaction>
    findByCardIdAndTransactionDateBetweenOrderByTransactionDateAsc(
            Long cardId,
            LocalDateTime from,
            LocalDateTime to
    );

    // =========================================================
    // 1. TOTAL TRANSACTION COUNT
    // =========================================================

    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           """)
    Long getTotalTransactionCount();

    // =========================================================
    // 2. TOTAL PURCHASE AMOUNT
    // =========================================================

    @Query("""
           SELECT COALESCE(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           """)
    BigDecimal getTotalPurchaseAmount();

    // =========================================================
    // 3. TOTAL PAYMENT AMOUNT
    // =========================================================

    @Query("""
           SELECT COALESCE(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PAYMENT'
           AND t.transactionStatus = 'SUCCESS'
           """)
    BigDecimal getTotalPaymentAmount();

    // =========================================================
    // 4. AVERAGE PURCHASE AMOUNT
    // =========================================================

    @Query("""
           SELECT COALESCE(AVG(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           """)
    BigDecimal getAveragePurchaseAmount();

    // =========================================================
    // 5. LARGEST SUCCESSFUL PURCHASE
    // =========================================================

    @Query("""
           SELECT COALESCE(MAX(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           """)
    BigDecimal getLargestPurchaseAmount();

    // =========================================================
    // 6. SUCCESSFUL PURCHASE COUNT
    // =========================================================

    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           """)
    Long getSuccessfulPurchaseCount();

    // =========================================================
    // 7. SUCCESSFUL PAYMENT COUNT
    // =========================================================

    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           WHERE t.transactionType = 'PAYMENT'
           AND t.transactionStatus = 'SUCCESS'
           """)
    Long getSuccessfulPaymentCount();

    // =========================================================
    // 8. FAILED TRANSACTION COUNT
    // =========================================================

    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           WHERE t.transactionStatus = 'FAILED'
           """)
    Long getFailedTransactionCount();

    // =========================================================
    // 9. MERCHANT SALES
    // =========================================================

    @Query("""
           SELECT new com.ofss.dto.report.MerchantSalesReport(
               t.merchantId,
               SUM(t.amount)
           )
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           AND t.merchantId IS NOT NULL
           GROUP BY t.merchantId
           ORDER BY SUM(t.amount) DESC
           """)
    List<MerchantSalesReport> getMerchantSales();

    // =========================================================
    // 10. MERCHANT TRANSACTION COUNT
    // =========================================================

    @Query("""
           SELECT new com.ofss.dto.report.MerchantTransactionCountReport(
               t.merchantId,
               COUNT(t)
           )
           FROM Transaction t
           WHERE t.merchantId IS NOT NULL
           GROUP BY t.merchantId
           ORDER BY COUNT(t) DESC
           """)
    List<MerchantTransactionCountReport>
    getMerchantTransactionCount();

    // =========================================================
    // 11. CARD SPENDING
    // =========================================================

    @Query("""
           SELECT new com.ofss.dto.report.CardSpendingReport(
               t.cardId,
               SUM(t.amount)
           )
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           GROUP BY t.cardId
           ORDER BY SUM(t.amount) DESC
           """)
    List<CardSpendingReport> getCardSpending();

    // =========================================================
    // 12. MONTHLY SPENDING
    // =========================================================

    @Query(value = """
           SELECT
               TO_CHAR(TRANSACTION_DATE, 'YYYY-MM') AS month,
               SUM(AMOUNT) AS total_spending
           FROM TRANSACTION_INFO
           WHERE TRANSACTION_TYPE = 'PURCHASE'
           AND TRANSACTION_STATUS = 'SUCCESS'
           GROUP BY TO_CHAR(TRANSACTION_DATE, 'YYYY-MM')
           ORDER BY month
           """,
           nativeQuery = true)
    List<Object[]> getMonthlySpending();

    // =========================================================
    // 13. DAILY TRANSACTION COUNT
    // =========================================================

    @Query(value = """
           SELECT
               TO_CHAR(TRANSACTION_DATE, 'YYYY-MM-DD')
                   AS transaction_date,
               COUNT(*) AS transaction_count
           FROM TRANSACTION_INFO
           GROUP BY TO_CHAR(
               TRANSACTION_DATE,
               'YYYY-MM-DD'
           )
           ORDER BY transaction_date
           """,
           nativeQuery = true)
    List<Object[]> getDailyTransactionCount();

    // =========================================================
    // 14. TRANSACTION TYPE SUMMARY
    // =========================================================

    @Query("""
           SELECT new com.ofss.dto.report.TransactionTypeSummaryReport(
               t.transactionType,
               COUNT(t),
               SUM(t.amount)
           )
           FROM Transaction t
           WHERE t.transactionStatus = 'SUCCESS'
           GROUP BY t.transactionType
           ORDER BY SUM(t.amount) DESC
           """)
    List<TransactionTypeSummaryReport>
    getTransactionTypeSummary();

    // =========================================================
    // 15. FAILURE REASON SUMMARY
    // =========================================================

    @Query("""
           SELECT t.failureReason, COUNT(t)
           FROM Transaction t
           WHERE t.transactionStatus = 'FAILED'
           AND t.failureReason IS NOT NULL
           GROUP BY t.failureReason
           ORDER BY COUNT(t) DESC
           """)
    List<Object[]> getFailureReasonSummary();

    // =========================================================
    // 16. MOST FREQUENTLY USED CARD
    // =========================================================

    @Query("""
           SELECT new com.ofss.dto.report.CardUsageReport(
               t.cardId,
               COUNT(t)
           )
           FROM Transaction t
           WHERE t.transactionStatus = 'SUCCESS'
           GROUP BY t.cardId
           ORDER BY COUNT(t) DESC
           """)
    List<CardUsageReport> getCardUsageDescending();

    // =========================================================
    // 17. LEAST FREQUENTLY USED CARD
    // =========================================================

    @Query("""
           SELECT new com.ofss.dto.report.CardUsageReport(
               t.cardId,
               COUNT(t)
           )
           FROM Transaction t
           WHERE t.transactionStatus = 'SUCCESS'
           GROUP BY t.cardId
           ORDER BY COUNT(t) ASC
           """)
    List<CardUsageReport> getCardUsageAscending();

    // =========================================================
    // 18. TODAY'S TOTAL PURCHASE AMOUNT
    // =========================================================

    @Query("""
           SELECT COALESCE(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           AND t.transactionDate >= :startOfDay
           AND t.transactionDate < :startOfNextDay
           """)
    BigDecimal getTodayPurchaseAmount(
            LocalDateTime startOfDay,
            LocalDateTime startOfNextDay
    );

    // =========================================================
    // 19. TODAY'S TOTAL PAYMENT AMOUNT
    // =========================================================

    @Query("""
           SELECT COALESCE(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PAYMENT'
           AND t.transactionStatus = 'SUCCESS'
           AND t.transactionDate >= :startOfDay
           AND t.transactionDate < :startOfNextDay
           """)
    BigDecimal getTodayPaymentAmount(
            LocalDateTime startOfDay,
            LocalDateTime startOfNextDay
    );

    // =========================================================
    // 20. CUSTOMER SPENDING
    // =========================================================

    @Query("""
           SELECT new com.ofss.dto.report.CustomerSpendingReport(
               t.customerId,
               SUM(t.amount)
           )
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           AND t.customerId IS NOT NULL
           GROUP BY t.customerId
           ORDER BY SUM(t.amount) DESC
           """)
    List<CustomerSpendingReport> getCustomerSpending();

    // =========================================================
    // 21. CUSTOMER PAYMENTS
    // =========================================================

    @Query("""
           SELECT new com.ofss.dto.report.CustomerSpendingReport(
               t.customerId,
               SUM(t.amount)
           )
           FROM Transaction t
           WHERE t.transactionType = 'PAYMENT'
           AND t.transactionStatus = 'SUCCESS'
           AND t.customerId IS NOT NULL
           GROUP BY t.customerId
           ORDER BY SUM(t.amount) DESC
           """)
    List<CustomerSpendingReport> getCustomerPayments();

    // =========================================================
    // 22. MONTHLY SPENDING OF EVERY CUSTOMER
    // =========================================================

    @Query(value = """
           SELECT
               CUSTOMER_ID,
               TO_CHAR(TRANSACTION_DATE, 'YYYY-MM') AS month,
               SUM(AMOUNT) AS total_spending
           FROM TRANSACTION_INFO
           WHERE TRANSACTION_TYPE = 'PURCHASE'
           AND TRANSACTION_STATUS = 'SUCCESS'
           AND CUSTOMER_ID IS NOT NULL
           GROUP BY
               CUSTOMER_ID,
               TO_CHAR(TRANSACTION_DATE, 'YYYY-MM')
           ORDER BY
               CUSTOMER_ID,
               month
           """,
           nativeQuery = true)
    List<Object[]> getCustomerMonthlySpending();
}