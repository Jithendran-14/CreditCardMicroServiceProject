package com.ofss.dto.report;

public class CardUsageReport {

    private Long cardId;
    private Long transactionCount;

    public CardUsageReport(Long cardId, Long transactionCount) {
        this.cardId = cardId;
        this.transactionCount = transactionCount;
    }

    public Long getCardId() {
        return cardId;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }
}