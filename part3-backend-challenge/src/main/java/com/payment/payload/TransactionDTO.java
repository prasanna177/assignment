package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO for complete transaction information with nested details
 */
@RecordBuilder
@Introspected
@Serdeable
public record TransactionDTO(
        Long txnId,
        BigDecimal amount,
        String currency,
        String status,
        Instant timestamp,
        String cardType,
        String cardLast4,
        String acquirer,
        String issuer,
        List<TransactionDetailDTO> details
) {
}
