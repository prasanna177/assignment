package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * DTO for pagination information
 */
@RecordBuilder
@Introspected
@Serdeable
public record PaginationDTO(
        Integer page,
        Integer size,
        Integer totalPages,
        Long totalElements
) {
}
