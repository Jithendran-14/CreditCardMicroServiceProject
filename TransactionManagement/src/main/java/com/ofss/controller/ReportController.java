package com.ofss.controller;

import com.ofss.service.ReportService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.ofss.dto.report.CardSpendingReport;
import com.ofss.dto.report.DailyTransactionReport;
import com.ofss.dto.report.FailureReasonReport;
import com.ofss.dto.report.MerchantSalesReport;
import com.ofss.dto.report.MerchantTransactionCountReport;
import com.ofss.dto.report.MonthlySpendingReport;
import com.ofss.dto.report.TransactionTypeSummaryReport;

import java.util.List;
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
    
    @GetMapping("/merchant-sales")
    public ResponseEntity<List<MerchantSalesReport>>
    getMerchantSales() {

        return ResponseEntity.ok(
                reportService.getMerchantSales()
        );
    }

    @GetMapping("/merchant-transaction-count")
    public ResponseEntity<List<MerchantTransactionCountReport>>
    getMerchantTransactionCount() {

        return ResponseEntity.ok(
                reportService.getMerchantTransactionCount()
        );
    }


    @GetMapping("/card-spending")
    public ResponseEntity<List<CardSpendingReport>>
    getCardSpending() {

        return ResponseEntity.ok(
                reportService.getCardSpending()
        );
    }

    @GetMapping("/daily-transactions")
    public ResponseEntity<List<DailyTransactionReport>>
    getDailyTransactions() {

        return ResponseEntity.ok(
                reportService.getDailyTransactionCount()
        );
    }

    @GetMapping("/transaction-type-summary")
    public ResponseEntity<List<TransactionTypeSummaryReport>>
    getTransactionTypeSummary() {

        return ResponseEntity.ok(
                reportService.getTransactionTypeSummary()
        );
    }
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
    @GetMapping("/failure-reasons")
    public ResponseEntity<List<FailureReasonReport>>
    getFailureReasonSummary() {

        return ResponseEntity.ok(
                reportService
                        .getFailureReasonSummary()
        );
    }
}