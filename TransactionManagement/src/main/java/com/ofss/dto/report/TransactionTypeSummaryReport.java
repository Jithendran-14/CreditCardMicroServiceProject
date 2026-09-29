package com.ofss.dto.report;

import java.math.BigDecimal;

public class TransactionTypeSummaryReport {

    private String transactionType;
    private Long transactionCount;
    private BigDecimal totalAmount;

    public TransactionTypeSummaryReport() {
    }

    public TransactionTypeSummaryReport(
            String transactionType,
            Long transactionCount,
            BigDecimal totalAmount) {

        this.transactionType = transactionType;
        this.transactionCount = transactionCount;
        this.totalAmount = totalAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}