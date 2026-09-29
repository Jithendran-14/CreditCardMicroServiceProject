
package com.ofss.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CREDIT_CARD")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARD_ID")
    private Long cardId;

    @Column(name = "CUSTOMER_ID", nullable = false)
    private Long customerId;

    @NotBlank(message = "Card number is required")
    @Pattern(
        regexp = "\\d{16}",
        message = "Card number must contain exactly 16 digits"
    )
    @Column(name = "CARD_NUMBER")
    private String cardNumber;

    @NotBlank(message = "Card type is required")
    @Pattern(
        regexp = "SILVER|GOLD|PLATINUM",
        message = "Card type must be SILVER, GOLD or PLATINUM"
    )
    @Column(name = "CARD_TYPE")
    private String cardType;

    @NotNull(message = "Credit limit is required")
    @Positive(message = "Credit limit must be greater than zero")
    @Column(name = "CREDIT_LIMIT")
    private BigDecimal creditLimit;

    @Column(name = "AVAILABLE_CREDIT", nullable = false, precision = 15, scale = 2)
    private BigDecimal availableCredit;

    @Column(name = "OUTSTANDING_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal outstandingAmount;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column(name = "CARD_STATUS", nullable = false, length = 10)
    private String cardStatus;

    public CreditCard() {
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(BigDecimal availableCredit) {
        this.availableCredit = availableCredit;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "cardId=" + cardId +
                ", customerId=" + customerId +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardType='" + cardType + '\'' +
                ", creditLimit=" + creditLimit +
                ", availableCredit=" + availableCredit +
                ", outstandingAmount=" + outstandingAmount +
                ", expiryDate=" + expiryDate +
                ", cardStatus='" + cardStatus + '\'' +
                '}';
    }
}

