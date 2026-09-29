package com.ofss.service;

import com.ofss.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import com.ofss.dto.report.CardSpendingReport;
import com.ofss.dto.report.DailyTransactionReport;
import com.ofss.dto.report.FailureReasonReport;
import com.ofss.dto.report.MerchantSalesReport;
import com.ofss.dto.report.MerchantTransactionCountReport;
import com.ofss.dto.report.MonthlySpendingReport;
import com.ofss.dto.report.TransactionTypeSummaryReport;

import java.util.ArrayList;
import java.util.List;

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
    
    public List<MerchantSalesReport> getMerchantSales() {

        return transactionRepository.getMerchantSales();
    }
    public List<MerchantTransactionCountReport>
    getMerchantTransactionCount() {

        return transactionRepository.getMerchantTransactionCount();
    }
    public List<CardSpendingReport> getCardSpending() {

        return transactionRepository.getCardSpending();
    }
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
    public List<TransactionTypeSummaryReport>
    getTransactionTypeSummary() {

        return transactionRepository.getTransactionTypeSummary();
    }
    public CardSpendingReport getTopSpendingCard() {

        List<CardSpendingReport> reports =
                transactionRepository.getCardSpending();

        if (reports.isEmpty()) {
            return null;
        }

        return reports.get(0);
    }
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
}
    
    

