package com.payment.payload;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;


@RecordBuilder
@Introspected
@Serdeable
public record SearchResponse<T>(
        int currentPage,
        int totalRecord,
        int pageSize,
        int totalPage,
        List<T> data
) {}
