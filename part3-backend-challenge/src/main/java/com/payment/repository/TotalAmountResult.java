package com.payment.repository;

import io.micronaut.core.annotation.Introspected;
import java.math.BigDecimal;

/**
 * DTO for total amount query results
 */
@Introspected
public class TotalAmountResult {
    private BigDecimal totalAmount;

    public TotalAmountResult() {
    }

    public TotalAmountResult(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
