package com.payment.search;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.Map;

@RecordBuilder
public record SearchModel(
        String sqlQuery,
        Map<Integer, Object> queryValues
) {
}
