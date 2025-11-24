package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for transaction summary statistics
 */
@RecordBuilder
@Introspected
@Serdeable
public record TransactionSummaryDTO(
        Long totalTransactions,
        BigDecimal totalAmount,
        String currency,
        Map<String, Long> byStatus
) {
}
