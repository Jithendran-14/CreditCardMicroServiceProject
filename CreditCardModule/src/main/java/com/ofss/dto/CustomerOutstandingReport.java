package com.ofss.dto;

import java.math.BigDecimal;

public interface CustomerOutstandingReport {

    Long getCustomerId();

    BigDecimal getTotalOutstandingAmount();
}