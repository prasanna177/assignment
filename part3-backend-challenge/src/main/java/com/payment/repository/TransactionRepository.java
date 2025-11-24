package com.payment.repository;

import com.payment.entity.TransactionMaster;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for TransactionMaster entities.
 */
@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TransactionRepository extends CrudRepository<TransactionMaster, Long> {

    // Example: Basic finder method (provided)
    List<TransactionMaster> findByMerchantId(String merchantId);

    /**
     * Find transactions by merchant ID with optional date range and status filtering
     * Supports pagination
     */
    @Query(value = """
            SELECT tm.* FROM operators.transaction_master tm
            WHERE tm.merchant_id = :merchantId
            AND (CAST(:startDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR tm.local_txn_date_time >= CAST(:startDate AS TIMESTAMP WITH TIME ZONE))
            AND (CAST(:endDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR tm.local_txn_date_time <= CAST(:endDate AS TIMESTAMP WITH TIME ZONE))
            AND (CAST(:status AS VARCHAR) IS NULL OR tm.status = CAST(:status AS VARCHAR))
            ORDER BY tm.local_txn_date_time DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM operators.transaction_master tm
            WHERE tm.merchant_id = :merchantId
            AND (CAST(:startDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR tm.local_txn_date_time >= CAST(:startDate AS TIMESTAMP WITH TIME ZONE))
            AND (CAST(:endDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR tm.local_txn_date_time <= CAST(:endDate AS TIMESTAMP WITH TIME ZONE))
            AND (CAST(:status AS VARCHAR) IS NULL OR tm.status = CAST(:status AS VARCHAR))
            """)
    Page<TransactionMaster> findByMerchantIdWithFilters(
            String merchantId,
            @Nullable Instant startDate,
            @Nullable Instant endDate,
            @Nullable String status,
            Pageable pageable
    );

    /**
     * Get transaction count by status for a merchant with optional date filtering
     */
    @Query(value = """
            SELECT tm.status, COUNT(*) as count
            FROM operators.transaction_master tm
            WHERE tm.merchant_id = :merchantId
            AND (CAST(:startDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR tm.local_txn_date_time >= CAST(:startDate AS TIMESTAMP WITH TIME ZONE))
            AND (CAST(:endDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR tm.local_txn_date_time <= CAST(:endDate AS TIMESTAMP WITH TIME ZONE))
            GROUP BY tm.status
            """)
    List<StatusCountResult> countByStatus(
            String merchantId,
            @Nullable Instant startDate,
            @Nullable Instant endDate
    );

    /**
     * Get total transaction amount for a merchant with optional date and status filtering
     */
    @Query(value = """
            SELECT COALESCE(SUM(tm.amount), 0) as total_amount
            FROM operators.transaction_master tm
            WHERE tm.merchant_id = :merchantId
            AND (CAST(:startDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR tm.local_txn_date_time >= CAST(:startDate AS TIMESTAMP WITH TIME ZONE))
            AND (CAST(:endDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR tm.local_txn_date_time <= CAST(:endDate AS TIMESTAMP WITH TIME ZONE))
            AND (CAST(:status AS VARCHAR) IS NULL OR tm.status = CAST(:status AS VARCHAR))
            """)
    TotalAmountResult getTotalAmount(
            String merchantId,
            @Nullable Instant startDate,
            @Nullable Instant endDate,
            @Nullable String status
    );
}
