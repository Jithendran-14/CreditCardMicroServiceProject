package com.ofss.dto.report;

public class DailyTransactionReport {

    private String transactionDate;
    private Long transactionCount;

    public DailyTransactionReport() {
    }

    public DailyTransactionReport(
            String transactionDate,
            Long transactionCount) {

        this.transactionDate = transactionDate;
        this.transactionCount = transactionCount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }
}