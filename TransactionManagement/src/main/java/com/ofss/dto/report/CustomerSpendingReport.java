package com.ofss.dto.report;

import java.math.BigDecimal;

public class CustomerSpendingReport {

    private Long customerId;
    private BigDecimal totalSpending;

    public CustomerSpendingReport() {
    }

    public CustomerSpendingReport(
            Long customerId,
            BigDecimal totalSpending) {

        this.customerId = customerId;
        this.totalSpending = totalSpending;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(BigDecimal totalSpending) {
        this.totalSpending = totalSpending;
    }
}