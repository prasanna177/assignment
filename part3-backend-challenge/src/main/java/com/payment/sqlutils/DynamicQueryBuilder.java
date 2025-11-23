package com.payment.sqlutils;

import com.payment.search.SearchModel;
import com.payment.search.SearchModelBuilder;

import java.util.HashMap;
import java.util.Map;

public class DynamicQueryBuilder {

    public static SearchModel getSearchModel(QueryCriteria queryCriteria) {
        var query = queryCriteria.sql();
        Map<Integer, Object> values = new HashMap();
        StringBuilder queryBuilder = new StringBuilder(query);

        if (queryCriteria.field() != null && !queryCriteria.field().isBlank()) {
            queryBuilder.append(" AND ");
            queryBuilder.append(queryCriteria.field() + " ILIKE ?");
            values.put(1, "%" + queryCriteria.value() + "%");
        }

        if (queryCriteria.sortField() != null && !queryCriteria.sortField().isBlank()) {
            queryBuilder.append(" ORDER BY ");
            queryBuilder.append(queryCriteria.sortField()).append(" ");
            queryBuilder.append(queryCriteria.sortOrder());
        }
        if (queryCriteria.pageSize() != 0) {
            queryBuilder.append(" LIMIT ");
            queryBuilder.append(queryCriteria.pageSize());
            queryBuilder.append(" OFFSET ");
            queryBuilder.append(queryCriteria.startingIndex());
        }
        return SearchModelBuilder.builder()
                .sqlQuery(queryBuilder.toString())
                .queryValues(values)
                .build();

    }
}
