package com.payment.repository;
import com.payment.search.SearchModel;
import com.payment.sqlutils.DynamicQueryBuilder;
import com.payment.sqlutils.QueryCriteria;
import com.payment.sqlutils.QueryCriteriaBuilder;
import com.payment.utils.HelperUtils;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import com.payment.entity.Merchant;
import com.payment.payload.SearchRequestPayload;
import io.micronaut.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public abstract class MerchantRepositoryImpl implements MerchantRepository {
    private final JdbcOperations jdbcOperations;

    public MerchantRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Transactional
    public List<Merchant> dynamicSearch(SearchRequestPayload payload) {
        String sql = "SELECT * FROM operators.merchant WHERE 1=1 ";
        QueryCriteria queryCriteria = QueryCriteriaBuilder.builder()
                .sql(sql)
                .field(payload.searchParameter() != null ? this.prepareConcatanatedFieldForSearch() : null)
                .value(payload.searchParameter())
                .startingIndex(HelperUtils.getStartingIndex(payload.pageNumber(), payload.pageSize()))
                .pageSize(payload.pageSize())
                .sortField(payload.sortField())
                .sortOrder(payload.sortOrder())
                .build();
        SearchModel searchModel = DynamicQueryBuilder.getSearchModel(queryCriteria);
        return jdbcOperations.prepareStatement(searchModel.sqlQuery(),
                statement -> {
                    searchModel.queryValues()
                            .forEach((key, value) -> {
                                try {
                                    if (value instanceof String) {
                                        statement.setString(key, (String) value);
                                    }
                                    if (value instanceof Integer) {
                                        statement.setInt(key, (Integer) value);
                                    }

                                    if (value instanceof Long) {
                                        statement.setLong(key, (Long) value);
                                    }

                                } catch (SQLException sqlException) {
                                    sqlException.printStackTrace();
                                    throw new RuntimeException("Database error occurred while processing the request", sqlException);
                                }
                            });
                    ResultSet resultSet = statement.executeQuery();
                    return jdbcOperations.entityStream(resultSet, Merchant.class)
                            .collect(Collectors.toList());
                });
    }

    @Transactional
    public int countDynamicSearch(SearchRequestPayload payload) {
        String sql = "SELECT count(*) FROM operators.merchant WHERE 1=1 ";
        QueryCriteria queryCriteria = QueryCriteriaBuilder.builder()
                .sql(sql)
                .field(this.prepareConcatanatedFieldForSearch())
                .value(payload.searchParameter())
                .build();
        SearchModel searchModel = DynamicQueryBuilder.getSearchModel(queryCriteria);
        return jdbcOperations.prepareStatement(searchModel.sqlQuery(),
                statement -> {
                    searchModel.queryValues()
                            .forEach((key, value) -> {
                                try {
                                    if (value instanceof String) {
                                        statement.setString(key, (String) value);
                                    }
                                    if (value instanceof Integer) {
                                        statement.setInt(key, (Integer) value);
                                    }

                                    if (value instanceof Long) {
                                        statement.setLong(key, (Long) value);
                                    }

                                } catch (SQLException sqlException) {
                                    sqlException.printStackTrace();
                                    throw new RuntimeException("Database error occurred while processing the request", sqlException);
                                }
                            });
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    } else {
                        return 0;
                    }
                });
    }

    private String prepareConcatanatedFieldForSearch() {
        return "lower(concat(id,name,email,phone,status))";
    }

}
