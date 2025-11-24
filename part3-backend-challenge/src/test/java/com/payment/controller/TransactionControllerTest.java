package com.payment.controller;

import com.payment.payload.*;
import com.payment.usecase.TransactionListUseCase;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    private TransactionController transactionController;
    private TransactionListUseCase transactionListUseCase;

    @BeforeEach
    void setUp() {
        transactionListUseCase = mock(TransactionListUseCase.class);
        transactionController = new TransactionController(transactionListUseCase);
    }

    @Test
    void getTransactions_WithValidMerchantId_ReturnsOk() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionListResponse mockResponse = createMockResponse(merchantId);
        
        when(transactionListUseCase.execute(any(TransactionListRequest.class)))
                .thenReturn(Optional.of(mockResponse));

        // Act
        HttpResponse<TransactionListResponse> response = transactionController.getTransactions(
                merchantId,
                null,
                null,
                null,
                null,
                null
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(merchantId, response.body().merchantId());
        
        // Verify use case was called with correct defaults
        ArgumentCaptor<TransactionListRequest> captor = ArgumentCaptor.forClass(TransactionListRequest.class);
        verify(transactionListUseCase).execute(captor.capture());
        
        TransactionListRequest capturedRequest = captor.getValue();
        assertEquals(merchantId, capturedRequest.merchantId());
        assertEquals(0, capturedRequest.page());
        assertEquals(20, capturedRequest.size());
        assertNull(capturedRequest.startDate());
        assertNull(capturedRequest.endDate());
        assertNull(capturedRequest.status());
    }

    @Test
    void getTransactions_WithPaginationParameters_UsesPagination() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionListResponse mockResponse = createMockResponse(merchantId);
        
        when(transactionListUseCase.execute(any(TransactionListRequest.class)))
                .thenReturn(Optional.of(mockResponse));

        // Act
        HttpResponse<TransactionListResponse> response = transactionController.getTransactions(
                merchantId,
                2,
                10,
                null,
                null,
                null
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        
        ArgumentCaptor<TransactionListRequest> captor = ArgumentCaptor.forClass(TransactionListRequest.class);
        verify(transactionListUseCase).execute(captor.capture());
        
        TransactionListRequest capturedRequest = captor.getValue();
        assertEquals(2, capturedRequest.page());
        assertEquals(10, capturedRequest.size());
    }

    @Test
    void getTransactions_WithDateFilters_UsesDateRange() {
        // Arrange
        String merchantId = "MCH-00001";
        String startDateStr = "2025-11-16T00:00:00Z";
        String endDateStr = "2025-11-18T23:59:59Z";
        TransactionListResponse mockResponse = createMockResponse(merchantId);
        
        when(transactionListUseCase.execute(any(TransactionListRequest.class)))
                .thenReturn(Optional.of(mockResponse));

        // Act
        HttpResponse<TransactionListResponse> response = transactionController.getTransactions(
                merchantId,
                null,
                null,
                startDateStr,
                endDateStr,
                null
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        
        ArgumentCaptor<TransactionListRequest> captor = ArgumentCaptor.forClass(TransactionListRequest.class);
        verify(transactionListUseCase).execute(captor.capture());
        
        TransactionListRequest capturedRequest = captor.getValue();
        assertNotNull(capturedRequest.startDate());
        assertNotNull(capturedRequest.endDate());
        assertEquals(Instant.parse(startDateStr), capturedRequest.startDate());
        assertEquals(Instant.parse(endDateStr), capturedRequest.endDate());
    }

    @Test
    void getTransactions_WithStatusFilter_UsesStatus() {
        // Arrange
        String merchantId = "MCH-00001";
        String status = "completed";
        TransactionListResponse mockResponse = createMockResponse(merchantId);
        
        when(transactionListUseCase.execute(any(TransactionListRequest.class)))
                .thenReturn(Optional.of(mockResponse));

        // Act
        HttpResponse<TransactionListResponse> response = transactionController.getTransactions(
                merchantId,
                null,
                null,
                null,
                null,
                status
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        
        ArgumentCaptor<TransactionListRequest> captor = ArgumentCaptor.forClass(TransactionListRequest.class);
        verify(transactionListUseCase).execute(captor.capture());
        
        TransactionListRequest capturedRequest = captor.getValue();
        assertEquals(status, capturedRequest.status());
    }

    @Test
    void getTransactions_WhenNoTransactionsFound_ReturnsNotFound() {
        // Arrange
        String merchantId = "INVALID-MERCHANT";
        
        when(transactionListUseCase.execute(any(TransactionListRequest.class)))
                .thenReturn(Optional.empty());

        // Act
        HttpResponse<TransactionListResponse> response = transactionController.getTransactions(
                merchantId,
                null,
                null,
                null,
                null,
                null
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void getTransactions_WithAllParameters_CombinesAllFilters() {
        // Arrange
        String merchantId = "MCH-00001";
        String startDateStr = "2025-11-16T00:00:00Z";
        String endDateStr = "2025-11-18T23:59:59Z";
        String status = "completed";
        TransactionListResponse mockResponse = createMockResponse(merchantId);
        
        when(transactionListUseCase.execute(any(TransactionListRequest.class)))
                .thenReturn(Optional.of(mockResponse));

        // Act
        HttpResponse<TransactionListResponse> response = transactionController.getTransactions(
                merchantId,
                1,
                15,
                startDateStr,
                endDateStr,
                status
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        
        ArgumentCaptor<TransactionListRequest> captor = ArgumentCaptor.forClass(TransactionListRequest.class);
        verify(transactionListUseCase).execute(captor.capture());
        
        TransactionListRequest capturedRequest = captor.getValue();
        assertEquals(merchantId, capturedRequest.merchantId());
        assertEquals(1, capturedRequest.page());
        assertEquals(15, capturedRequest.size());
        assertEquals(Instant.parse(startDateStr), capturedRequest.startDate());
        assertEquals(Instant.parse(endDateStr), capturedRequest.endDate());
        assertEquals(status, capturedRequest.status());
    }

    private TransactionListResponse createMockResponse(String merchantId) {
        // Create mock transaction detail
        TransactionDetailDTO detail = new TransactionDetailDTO(
                1L,
                "fee",
                new BigDecimal("3.50"),
                "Processing fee"
        );

        // Create mock transaction
        TransactionDTO transaction = new TransactionDTO(
                1L,
                new BigDecimal("150.00"),
                "USD",
                "completed",
                Instant.parse("2025-11-18T14:32:15Z"),
                "VISA",
                "4242",
                "Global Payment Services",
                "Visa Worldwide",
                List.of(detail)
        );

        // Create mock summary
        TransactionSummaryDTO summary = new TransactionSummaryDTO(
                1L,
                new BigDecimal("150.00"),
                "USD",
                Map.of("completed", 1L)
        );

        // Create pagination
        PaginationDTO pagination = new PaginationDTO(
                0,
                20,
                1,
                1L
        );

        // Create date range
        DateRangeDTO dateRange = new DateRangeDTO(
                Instant.parse("2025-11-18T14:32:15Z"),
                Instant.parse("2025-11-18T14:32:15Z")
        );

        return new TransactionListResponse(
                merchantId,
                dateRange,
                summary,
                List.of(transaction),
                pagination
        );
    }
}
