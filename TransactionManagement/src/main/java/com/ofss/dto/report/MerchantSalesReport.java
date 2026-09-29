package com.ofss.dto.report;

import java.math.BigDecimal;

public class MerchantSalesReport {

    private Long merchantId;
    private BigDecimal totalSales;

    public MerchantSalesReport() {
    }

    public MerchantSalesReport(Long merchantId, BigDecimal totalSales) {
        this.merchantId = merchantId;
        this.totalSales = totalSales;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }
}