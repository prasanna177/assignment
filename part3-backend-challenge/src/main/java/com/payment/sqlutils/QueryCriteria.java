package com.payment.sqlutils;


import io.micronaut.core.annotation.Introspected;
import io.soabase.recordbuilder.core.RecordBuilder;


@RecordBuilder
@Introspected
public record QueryCriteria (
        String sql,
        String field,
        Object value,
        long startingIndex,
        int pageSize,
        String sortField,
        String sortOrder
) {
}