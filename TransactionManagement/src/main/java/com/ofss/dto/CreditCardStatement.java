package com.ofss.dto;

import com.ofss.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreditCardStatement {

    private Long cardId;

    private LocalDateTime statementFrom;

    private LocalDateTime statementTo;

    private BigDecimal totalPurchaseAmount;

    private BigDecimal totalPaymentAmount;

    private BigDecimal netAmount;

    private Long purchaseCount;

    private Long paymentCount;

    private List<Transaction> transactions;

    public CreditCardStatement() {
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public LocalDateTime getStatementFrom() {
        return statementFrom;
    }

    public void setStatementFrom(LocalDateTime statementFrom) {
        this.statementFrom = statementFrom;
    }

    public LocalDateTime getStatementTo() {
        return statementTo;
    }

    public void setStatementTo(LocalDateTime statementTo) {
        this.statementTo = statementTo;
    }

    public BigDecimal getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(BigDecimal totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }

    public BigDecimal getTotalPaymentAmount() {
        return totalPaymentAmount;
    }

    public void setTotalPaymentAmount(BigDecimal totalPaymentAmount) {
        this.totalPaymentAmount = totalPaymentAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public Long getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(Long purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public Long getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(Long paymentCount) {
        this.paymentCount = paymentCount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}