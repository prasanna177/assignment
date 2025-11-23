package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
@Introspected
@Serdeable
public record SearchRequestPayload(
        @Nullable
        int pageNumber,
        @Nullable
        int pageSize,
        @Nullable
        String sortField,
        @Nullable
        String sortOrder,
        String searchParameter
) {

}