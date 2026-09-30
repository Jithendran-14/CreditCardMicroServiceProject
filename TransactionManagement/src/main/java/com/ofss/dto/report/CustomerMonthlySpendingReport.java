package com.ofss.dto.report;

import java.math.BigDecimal;

public class CustomerMonthlySpendingReport {

    private Long customerId;
    private String month;
    private BigDecimal totalSpending;

    public CustomerMonthlySpendingReport() {
    }

    public CustomerMonthlySpendingReport(
            Long customerId,
            String month,
            BigDecimal totalSpending) {

        this.customerId = customerId;
        this.month = month;
        this.totalSpending = totalSpending;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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