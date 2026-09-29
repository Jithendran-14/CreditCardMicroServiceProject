package com.ofss.dto.report;

import java.math.BigDecimal;

public class CardSpendingReport {

    private Long cardId;
    private BigDecimal totalSpending;

    public CardSpendingReport() {
    }

    public CardSpendingReport(
            Long cardId,
            BigDecimal totalSpending) {

        this.cardId = cardId;
        this.totalSpending = totalSpending;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public BigDecimal getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(BigDecimal totalSpending) {
        this.totalSpending = totalSpending;
    }
}