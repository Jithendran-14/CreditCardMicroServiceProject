package com.ofss.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PurchaseRequest {

    @NotNull(message = "Card ID is required")
    private Long cardId;

    @NotNull(message = "Merchant ID is required")
    private Long merchantId;

    @NotNull(message = "Purchase amount is required")
    @Positive(message = "Purchase amount must be greater than zero")
    private BigDecimal amount;

    public PurchaseRequest() {
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}