package com.payment.controller;

import com.payment.payload.TransactionListRequest;
import com.payment.payload.TransactionListResponse;
import com.payment.usecase.TransactionListUseCase;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;
import java.util.Optional;

/**
 * Transaction Controller - Handles transaction-related API endpoints
 */
@Controller("/api/v1/transactions")
@Tag(name = "Transactions")
public class TransactionController {

    private final TransactionListUseCase transactionListUseCase;

    public TransactionController(TransactionListUseCase transactionListUseCase) {
        this.transactionListUseCase = transactionListUseCase;
    }

    @Get("/{merchantId}/transactions")
    @Operation(
        summary = "Get merchant transactions",
        description = "Returns paginated list of transactions for a merchant with optional filtering by date range and status"
    )
    public HttpResponse<TransactionListResponse> getTransactions(
            @PathVariable String merchantId,
            @QueryValue @Nullable Integer page,
            @QueryValue @Nullable Integer size,
            @QueryValue @Nullable  String startDate,
            @QueryValue @Nullable  String endDate,
            @QueryValue @Nullable String status
    ) {
        // Parse dates if provided - handle both date-only and full ISO-8601 formats
        Instant startInstant = parseFlexibleDate(startDate, true);
        Instant endInstant = parseFlexibleDate(endDate, false);

        // Create request with defaults
        TransactionListRequest request = new TransactionListRequest(
                merchantId,
                page != null ? page : 0,
                size != null ? size : 20,
                startInstant,
                endInstant,
                status
        );

        // Execute use case
        Optional<TransactionListResponse> response = transactionListUseCase.execute(request);
        return response
                .map(HttpResponse::ok)
                .orElseGet(() -> HttpResponse.notFound());
    }

    /**
     * Parse date string flexibly - handles both date-only (2025-11-16) and full ISO-8601 (2025-11-16T00:00:00Z)
     * @param dateStr The date string to parse
     * @param isStartOfDay If true and dateStr is date-only, use start of day (00:00:00); otherwise use end of day (23:59:59)
     * @return Parsed Instant or null if dateStr is null
     */
    private Instant parseFlexibleDate(String dateStr, boolean isStartOfDay) {
        if (dateStr == null) {
            return null;
        }
        
        try {
            // Try parsing as full ISO-8601 instant first
            return Instant.parse(dateStr);
        } catch (Exception e) {
            // If that fails, try parsing as date-only and add time
            try {
                java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr);
                java.time.LocalTime time = isStartOfDay 
                    ? java.time.LocalTime.of(0, 0, 0) 
                    : java.time.LocalTime.of(23, 59, 59);
                return localDate.atTime(time).atZone(java.time.ZoneOffset.UTC).toInstant();
            } catch (Exception ex) {
                throw new IllegalArgumentException("Invalid date format. Expected ISO-8601 (2025-11-16T00:00:00Z) or date-only (2025-11-16)", ex);
            }
        }
    }
}
