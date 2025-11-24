package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;

/**
 * DTO for transaction detail information
 */
@RecordBuilder
@Introspected
@Serdeable
public record TransactionDetailDTO(
        Long detailId,
        String type,
        BigDecimal amount,
        String description
) {
}
