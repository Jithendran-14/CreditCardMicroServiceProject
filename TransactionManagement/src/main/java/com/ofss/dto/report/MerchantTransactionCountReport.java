package com.ofss.dto.report;

public class MerchantTransactionCountReport {

    private Long merchantId;
    private Long transactionCount;

    public MerchantTransactionCountReport() {
    }

    public MerchantTransactionCountReport(
            Long merchantId,
            Long transactionCount) {

        this.merchantId = merchantId;
        this.transactionCount = transactionCount;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }
}