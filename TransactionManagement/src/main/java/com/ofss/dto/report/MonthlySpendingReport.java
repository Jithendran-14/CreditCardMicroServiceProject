package com.ofss.dto.report;

import java.math.BigDecimal;

public class MonthlySpendingReport {

    private String month;
    private BigDecimal totalSpending;

    public MonthlySpendingReport() {
    }

    public MonthlySpendingReport(
            String month,
            BigDecimal totalSpending) {

        this.month = month;
        this.totalSpending = totalSpending;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(BigDecimal totalSpending) {
        this.totalSpending = totalSpending;
    }
}