package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;

/**
 * Request payload for transaction list API
 */
@Introspected
@Serdeable
public record TransactionListRequest(
        String merchantId,
        Integer page,
        Integer size,
        @Nullable Instant startDate,
        @Nullable Instant endDate,
        @Nullable String status
) {
    public TransactionListRequest {
        // Set defaults
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
    }
}
