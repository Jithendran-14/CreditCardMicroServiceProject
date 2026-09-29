package com.ofss.repository;

import com.ofss.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ofss.dto.report.CardSpendingReport;
import com.ofss.dto.report.DailyTransactionReport;
import com.ofss.dto.report.MerchantSalesReport;
import com.ofss.dto.report.MerchantTransactionCountReport;
import com.ofss.dto.report.MonthlySpendingReport;
import com.ofss.dto.report.TransactionTypeSummaryReport;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
                JpaSpecificationExecutor<Transaction> {

    // Existing filters

    List<Transaction> findByCardId(Long cardId);

    List<Transaction> findByTransactionType(String transactionType);

    List<Transaction> findByTransactionStatus(String transactionStatus);

    List<Transaction> findByMerchantId(Long merchantId);

    List<Transaction> findByTransactionDateBetween(
            LocalDateTime from,
            LocalDateTime to
    );


    // =========================================================
    // REPORTS
    // =========================================================

    // 1. Total number of transactions

    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           """)
    Long getTotalTransactionCount();


    // 2. Total purchase amount

    @Query("""
           SELECT COALESCE(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           """)
    BigDecimal getTotalPurchaseAmount();


    // 3. Total payment amount

    @Query("""
           SELECT COALESCE(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PAYMENT'
           AND t.transactionStatus = 'SUCCESS'
           """)
    BigDecimal getTotalPaymentAmount();


    // 4. Average purchase amount

    @Query("""
           SELECT COALESCE(AVG(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           """)
    BigDecimal getAveragePurchaseAmount();


    // 5. Largest successful purchase

    @Query("""
           SELECT COALESCE(MAX(t.amount), 0)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           """)
    BigDecimal getLargestPurchaseAmount();


    // 6. Total successful purchase count

    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           WHERE t.transactionType = 'PURCHASE'
           AND t.transactionStatus = 'SUCCESS'
           """)
    Long getSuccessfulPurchaseCount();


    // 7. Total successful payment count

    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           WHERE t.transactionType = 'PAYMENT'
           AND t.transactionStatus = 'SUCCESS'
           """)
    Long getSuccessfulPaymentCount();


    // 8. Total failed transactions

    @Query("""
           SELECT COUNT(t)
           FROM Transaction t
           WHERE t.transactionStatus = 'FAILED'
           """)
    Long getFailedTransactionCount();
    
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

    	@Query(value = """
    	       SELECT
    	           TO_CHAR(TRANSACTION_DATE, 'YYYY-MM-DD') AS transaction_date,
    	           COUNT(*) AS transaction_count
    	       FROM TRANSACTION_INFO
    	       GROUP BY TO_CHAR(TRANSACTION_DATE, 'YYYY-MM-DD')
    	       ORDER BY transaction_date
    	       """,
    	       nativeQuery = true)
    	List<Object[]> getDailyTransactionCount();

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
    	@Query("""
    		    SELECT t.failureReason, COUNT(t)
    		    FROM Transaction t
    		    WHERE t.transactionStatus = 'FAILED'
    		    AND t.failureReason IS NOT NULL
    		    GROUP BY t.failureReason
    		    ORDER BY COUNT(t) DESC
    		""")
    		List<Object[]> getFailureReasonSummary();
}

