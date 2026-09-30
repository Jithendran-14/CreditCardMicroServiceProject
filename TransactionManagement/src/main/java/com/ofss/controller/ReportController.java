package com.ofss.controller;

import com.ofss.dto.report.CardSpendingReport;
import com.ofss.dto.report.CardUsageReport;
import com.ofss.dto.report.CustomerMonthlySpendingReport;
import com.ofss.dto.report.CustomerSpendingReport;
import com.ofss.dto.report.DailyTransactionReport;
import com.ofss.dto.report.FailureReasonReport;
import com.ofss.dto.report.MerchantSalesReport;
import com.ofss.dto.report.MerchantTransactionCountReport;
import com.ofss.dto.report.MonthlySpendingReport;
import com.ofss.dto.report.TransactionTypeSummaryReport;
import com.ofss.service.ReportService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // =========================================================
    // 1. TOTAL TRANSACTION COUNT
    // =========================================================

    @GetMapping("/transaction-count")
    public ResponseEntity<Map<String, Object>>
    getTotalTransactionCount() {

        return ResponseEntity.ok(
                reportService.getTotalTransactionCount()
        );
    }

    // =========================================================
    // 2. TOTAL PURCHASE AMOUNT
    // =========================================================

    @GetMapping("/total-purchases")
    public ResponseEntity<Map<String, Object>>
    getTotalPurchaseAmount() {

        return ResponseEntity.ok(
                reportService.getTotalPurchaseAmount()
        );
    }

    // =========================================================
    // 3. TOTAL PAYMENT AMOUNT
    // =========================================================

    @GetMapping("/total-payments")
    public ResponseEntity<Map<String, Object>>
    getTotalPaymentAmount() {

        return ResponseEntity.ok(
                reportService.getTotalPaymentAmount()
        );
    }

    // =========================================================
    // 4. AVERAGE PURCHASE
    // =========================================================

    @GetMapping("/average-purchase")
    public ResponseEntity<Map<String, Object>>
    getAveragePurchaseAmount() {

        return ResponseEntity.ok(
                reportService.getAveragePurchaseAmount()
        );
    }

    // =========================================================
    // 5. LARGEST PURCHASE
    // =========================================================

    @GetMapping("/largest-purchase")
    public ResponseEntity<Map<String, Object>>
    getLargestPurchaseAmount() {

        return ResponseEntity.ok(
                reportService.getLargestPurchaseAmount()
        );
    }

    // =========================================================
    // 6. SUCCESSFUL PURCHASE COUNT
    // =========================================================

    @GetMapping("/successful-purchases")
    public ResponseEntity<Map<String, Object>>
    getSuccessfulPurchaseCount() {

        return ResponseEntity.ok(
                reportService.getSuccessfulPurchaseCount()
        );
    }

    // =========================================================
    // 7. SUCCESSFUL PAYMENT COUNT
    // =========================================================

    @GetMapping("/successful-payments")
    public ResponseEntity<Map<String, Object>>
    getSuccessfulPaymentCount() {

        return ResponseEntity.ok(
                reportService.getSuccessfulPaymentCount()
        );
    }

    // =========================================================
    // 8. FAILED TRANSACTION COUNT
    // =========================================================

    @GetMapping("/failed-transactions")
    public ResponseEntity<Map<String, Object>>
    getFailedTransactionCount() {

        return ResponseEntity.ok(
                reportService.getFailedTransactionCount()
        );
    }

    // =========================================================
    // 9. MERCHANT SALES
    // =========================================================

    @GetMapping("/merchant-sales")
    public ResponseEntity<List<MerchantSalesReport>>
    getMerchantSales() {

        return ResponseEntity.ok(
                reportService.getMerchantSales()
        );
    }

    // =========================================================
    // 10. MERCHANT TRANSACTION COUNT
    // =========================================================

    @GetMapping("/merchant-transaction-count")
    public ResponseEntity<List<MerchantTransactionCountReport>>
    getMerchantTransactionCount() {

        return ResponseEntity.ok(
                reportService.getMerchantTransactionCount()
        );
    }

    // =========================================================
    // 11. CARD SPENDING
    // =========================================================

    @GetMapping("/card-spending")
    public ResponseEntity<List<CardSpendingReport>>
    getCardSpending() {

        return ResponseEntity.ok(
                reportService.getCardSpending()
        );
    }

    // =========================================================
    // 12. DAILY TRANSACTION COUNT
    // =========================================================

    @GetMapping("/daily-transactions")
    public ResponseEntity<List<DailyTransactionReport>>
    getDailyTransactions() {

        return ResponseEntity.ok(
                reportService.getDailyTransactionCount()
        );
    }

    // =========================================================
    // 13. TRANSACTION TYPE SUMMARY
    // =========================================================

    @GetMapping("/transaction-type-summary")
    public ResponseEntity<List<TransactionTypeSummaryReport>>
    getTransactionTypeSummary() {

        return ResponseEntity.ok(
                reportService.getTransactionTypeSummary()
        );
    }

    // =========================================================
    // 14. TOP SPENDING CARD
    // =========================================================

    @GetMapping("/top-spending-card")
    public ResponseEntity<CardSpendingReport>
    getTopSpendingCard() {

        CardSpendingReport report =
                reportService.getTopSpendingCard();

        if (report == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }

    // =========================================================
    // 15. FAILURE REASON SUMMARY
    // =========================================================

    @GetMapping("/failure-reasons")
    public ResponseEntity<List<FailureReasonReport>>
    getFailureReasonSummary() {

        return ResponseEntity.ok(
                reportService.getFailureReasonSummary()
        );
    }

    // =========================================================
    // 16. MOST FREQUENTLY USED CARD
    // =========================================================

    @GetMapping("/most-frequently-used-card")
    public ResponseEntity<CardUsageReport>
    getMostFrequentlyUsedCard() {

        CardUsageReport report =
                reportService.getMostFrequentlyUsedCard();

        if (report == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }

    // =========================================================
    // 17. LEAST FREQUENTLY USED CARD
    // =========================================================

    @GetMapping("/least-frequently-used-card")
    public ResponseEntity<CardUsageReport>
    getLeastFrequentlyUsedCard() {

        CardUsageReport report =
                reportService.getLeastFrequentlyUsedCard();

        if (report == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }

    // =========================================================
    // 18. TODAY'S TOTAL PURCHASE AMOUNT
    // =========================================================

    @GetMapping("/today-purchases")
    public ResponseEntity<Map<String, Object>>
    getTodayPurchaseAmount() {

        return ResponseEntity.ok(
                reportService.getTodayPurchaseAmount()
        );
    }

    // =========================================================
    // 19. TODAY'S TOTAL PAYMENT AMOUNT
    // =========================================================

    @GetMapping("/today-payments")
    public ResponseEntity<Map<String, Object>>
    getTodayPaymentAmount() {

        return ResponseEntity.ok(
                reportService.getTodayPaymentAmount()
        );
    }

    // =========================================================
    // 20. CUSTOMER WITH HIGHEST SPENDING
    // =========================================================

    @GetMapping("/top-spending-customer")
    public ResponseEntity<CustomerSpendingReport>
    getTopSpendingCustomer() {

        CustomerSpendingReport report =
                reportService.getTopSpendingCustomer();

        if (report == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }

    // =========================================================
    // 21. CUSTOMER WITH HIGHEST PAYMENT
    // =========================================================

    @GetMapping("/top-payment-customer")
    public ResponseEntity<CustomerSpendingReport>
    getTopPaymentCustomer() {

        CustomerSpendingReport report =
                reportService.getTopPaymentCustomer();

        if (report == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }

    // =========================================================
    // 22. MONTHLY SPENDING OF EVERY CUSTOMER
    // =========================================================

    @GetMapping("/customer-monthly-spending")
    public ResponseEntity<List<CustomerMonthlySpendingReport>>
    getCustomerMonthlySpending() {

        return ResponseEntity.ok(
                reportService.getCustomerMonthlySpending()
        );
    }
}