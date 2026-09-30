package com.ofss.service;

import com.ofss.repository.TransactionRepository;

import com.ofss.dto.report.CustomerSpendingReport;
import com.ofss.dto.report.CustomerMonthlySpendingReport;
import com.ofss.dto.report.CardSpendingReport;
import com.ofss.dto.report.CardUsageReport;
import com.ofss.dto.report.DailyTransactionReport;
import com.ofss.dto.report.FailureReasonReport;
import com.ofss.dto.report.MerchantSalesReport;
import com.ofss.dto.report.MerchantTransactionCountReport;
import com.ofss.dto.report.MonthlySpendingReport;
import com.ofss.dto.report.TransactionTypeSummaryReport;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // =========================================================
    // 1. TOTAL TRANSACTION COUNT
    // =========================================================

    public Map<String, Object> getTotalTransactionCount() {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put(
                "totalTransactions",
                transactionRepository.getTotalTransactionCount()
        );

        return report;
    }

    // =========================================================
    // 2. TOTAL PURCHASE AMOUNT
    // =========================================================

    public Map<String, Object> getTotalPurchaseAmount() {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put(
                "totalPurchaseAmount",
                transactionRepository.getTotalPurchaseAmount()
        );

        return report;
    }

    // =========================================================
    // 3. TOTAL PAYMENT AMOUNT
    // =========================================================

    public Map<String, Object> getTotalPaymentAmount() {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put(
                "totalPaymentAmount",
                transactionRepository.getTotalPaymentAmount()
        );

        return report;
    }

    // =========================================================
    // 4. AVERAGE PURCHASE AMOUNT
    // =========================================================

    public Map<String, Object> getAveragePurchaseAmount() {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put(
                "averagePurchaseAmount",
                transactionRepository.getAveragePurchaseAmount()
        );

        return report;
    }

    // =========================================================
    // 5. LARGEST PURCHASE
    // =========================================================

    public Map<String, Object> getLargestPurchaseAmount() {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put(
                "largestPurchaseAmount",
                transactionRepository.getLargestPurchaseAmount()
        );

        return report;
    }

    // =========================================================
    // 6. SUCCESSFUL PURCHASE COUNT
    // =========================================================

    public Map<String, Object> getSuccessfulPurchaseCount() {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put(
                "successfulPurchaseCount",
                transactionRepository.getSuccessfulPurchaseCount()
        );

        return report;
    }

    // =========================================================
    // 7. SUCCESSFUL PAYMENT COUNT
    // =========================================================

    public Map<String, Object> getSuccessfulPaymentCount() {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put(
                "successfulPaymentCount",
                transactionRepository.getSuccessfulPaymentCount()
        );

        return report;
    }

    // =========================================================
    // 8. FAILED TRANSACTION COUNT
    // =========================================================

    public Map<String, Object> getFailedTransactionCount() {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put(
                "failedTransactionCount",
                transactionRepository.getFailedTransactionCount()
        );

        return report;
    }

    // =========================================================
    // 9. MERCHANT SALES
    // =========================================================

    public List<MerchantSalesReport> getMerchantSales() {

        return transactionRepository.getMerchantSales();
    }

    // =========================================================
    // 10. MERCHANT TRANSACTION COUNT
    // =========================================================

    public List<MerchantTransactionCountReport>
    getMerchantTransactionCount() {

        return transactionRepository.getMerchantTransactionCount();
    }

    // =========================================================
    // 11. CARD SPENDING
    // =========================================================

    public List<CardSpendingReport> getCardSpending() {

        return transactionRepository.getCardSpending();
    }

    // =========================================================
    // 12. MONTHLY SPENDING
    // =========================================================

    public List<MonthlySpendingReport> getMonthlySpending() {

        List<Object[]> results =
                transactionRepository.getMonthlySpending();

        List<MonthlySpendingReport> reports = new ArrayList<>();

        for (Object[] row : results) {

            String month = (String) row[0];

            BigDecimal totalSpending =
                    row[1] instanceof BigDecimal
                            ? (BigDecimal) row[1]
                            : new BigDecimal(row[1].toString());

            reports.add(
                    new MonthlySpendingReport(
                            month,
                            totalSpending
                    )
            );
        }

        return reports;
    }

    // =========================================================
    // 13. DAILY TRANSACTION COUNT
    // =========================================================

    public List<DailyTransactionReport>
    getDailyTransactionCount() {

        List<Object[]> results =
                transactionRepository.getDailyTransactionCount();

        List<DailyTransactionReport> reports = new ArrayList<>();

        for (Object[] row : results) {

            String transactionDate =
                    (String) row[0];

            Long transactionCount =
                    ((Number) row[1]).longValue();

            reports.add(
                    new DailyTransactionReport(
                            transactionDate,
                            transactionCount
                    )
            );
        }

        return reports;
    }

    // =========================================================
    // 14. TRANSACTION TYPE SUMMARY
    // =========================================================

    public List<TransactionTypeSummaryReport>
    getTransactionTypeSummary() {

        return transactionRepository.getTransactionTypeSummary();
    }

    // =========================================================
    // 15. TOP SPENDING CARD
    // =========================================================

    public CardSpendingReport getTopSpendingCard() {

        List<CardSpendingReport> reports =
                transactionRepository.getCardSpending();

        if (reports.isEmpty()) {
            return null;
        }

        return reports.get(0);
    }

    // =========================================================
    // 16. FAILURE REASON SUMMARY
    // =========================================================

    public List<FailureReasonReport> getFailureReasonSummary() {

        return transactionRepository
                .getFailureReasonSummary()
                .stream()
                .map(row -> new FailureReasonReport(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    // =========================================================
    // 17. MOST FREQUENTLY USED CARD
    // =========================================================

    public CardUsageReport getMostFrequentlyUsedCard() {

        List<CardUsageReport> reports =
                transactionRepository.getCardUsageDescending();

        if (reports.isEmpty()) {
            return null;
        }

        return reports.get(0);
    }

    // =========================================================
    // 18. LEAST FREQUENTLY USED CARD
    // =========================================================

    public CardUsageReport getLeastFrequentlyUsedCard() {

        List<CardUsageReport> reports =
                transactionRepository.getCardUsageAscending();

        if (reports.isEmpty()) {
            return null;
        }

        return reports.get(0);
    }

    // =========================================================
    // 19. TODAY'S TOTAL PURCHASE AMOUNT
    // =========================================================

    public Map<String, Object> getTodayPurchaseAmount() {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay =
                today.atStartOfDay();

        LocalDateTime startOfNextDay =
                today.plusDays(1).atStartOfDay();

        Map<String, Object> report =
                new LinkedHashMap<>();

        report.put(
                "date",
                today
        );

        report.put(
                "totalPurchaseAmount",
                transactionRepository.getTodayPurchaseAmount(
                        startOfDay,
                        startOfNextDay
                )
        );

        return report;
    }

    // =========================================================
    // 20. TODAY'S TOTAL PAYMENT AMOUNT
    // =========================================================

    public Map<String, Object> getTodayPaymentAmount() {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay =
                today.atStartOfDay();

        LocalDateTime startOfNextDay =
                today.plusDays(1).atStartOfDay();

        Map<String, Object> report =
                new LinkedHashMap<>();

        report.put(
                "date",
                today
        );

        report.put(
                "totalPaymentAmount",
                transactionRepository.getTodayPaymentAmount(
                        startOfDay,
                        startOfNextDay
                )
        );

        return report;
    }

    // =========================================================
    // 21. CUSTOMER WITH HIGHEST SPENDING
    // =========================================================

    public CustomerSpendingReport getTopSpendingCustomer() {

        List<CustomerSpendingReport> reports =
                transactionRepository.getCustomerSpending();

        if (reports.isEmpty()) {
            return null;
        }

        return reports.get(0);
    }

    // =========================================================
    // 22. CUSTOMER WITH HIGHEST PAYMENT
    // =========================================================

    public CustomerSpendingReport getTopPaymentCustomer() {

        List<CustomerSpendingReport> reports =
                transactionRepository.getCustomerPayments();

        if (reports.isEmpty()) {
            return null;
        }

        return reports.get(0);
    }

    // =========================================================
    // 23. MONTHLY SPENDING OF EVERY CUSTOMER
    // =========================================================

    public List<CustomerMonthlySpendingReport>
    getCustomerMonthlySpending() {

        List<Object[]> results =
                transactionRepository.getCustomerMonthlySpending();

        List<CustomerMonthlySpendingReport> reports =
                new ArrayList<>();

        for (Object[] row : results) {

            Long customerId =
                    ((Number) row[0]).longValue();

            String month =
                    (String) row[1];

            BigDecimal totalSpending =
                    row[2] instanceof BigDecimal
                            ? (BigDecimal) row[2]
                            : new BigDecimal(row[2].toString());

            reports.add(
                    new CustomerMonthlySpendingReport(
                            customerId,
                            month,
                            totalSpending
                    )
            );
        }

        return reports;
    }
}