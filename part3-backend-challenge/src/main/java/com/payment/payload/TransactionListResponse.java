package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

/**
 * Main response for transaction list API
 */
@RecordBuilder
@Introspected
@Serdeable
public record TransactionListResponse(
        String merchantId,
        DateRangeDTO dateRange,
        TransactionSummaryDTO summary,
        List<TransactionDTO> transactions,
        PaginationDTO pagination
) {
}
