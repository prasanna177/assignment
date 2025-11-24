package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.Instant;

/**
 * DTO for date range
 */
@RecordBuilder
@Introspected
@Serdeable
public record DateRangeDTO(
        Instant start,
        Instant end
) {
}
