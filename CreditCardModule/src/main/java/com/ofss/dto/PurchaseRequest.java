package com.ofss.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PurchaseRequest {

    @NotNull(message = "Purchase amount is required")
    @Positive(message = "Purchase amount must be greater than zero")
    private BigDecimal amount;

    public PurchaseRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}