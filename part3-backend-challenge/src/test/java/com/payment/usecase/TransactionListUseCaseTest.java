package com.payment.usecase;

import com.payment.entity.Member;
import com.payment.entity.TransactionDetail;
import com.payment.entity.TransactionMaster;
import com.payment.payload.*;
import com.payment.repository.MemberRepository;
import com.payment.repository.StatusCountResult;
import com.payment.repository.TotalAmountResult;
import com.payment.repository.TransactionDetailRepository;
import com.payment.repository.TransactionRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class TransactionListUseCaseTest {

    private TransactionListUseCase useCase;
    private TransactionRepository transactionRepository;
    private TransactionDetailRepository transactionDetailRepository;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        transactionDetailRepository = mock(TransactionDetailRepository.class);
        memberRepository = mock(MemberRepository.class);
        useCase = new TransactionListUseCase(
                transactionRepository,
                transactionDetailRepository,
                memberRepository
        );
    }

    @Test
    void execute_WithValidRequest_ReturnsTransactionListResponse() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionListRequest request = new TransactionListRequest(
                merchantId, 0, 20, null, null, null
        );

        TransactionMaster txn = createMockTransaction(1L, merchantId);
        Page<TransactionMaster> page = Page.of(List.of(txn), Pageable.from(0, 20), 1);
        
        when(transactionRepository.findByMerchantIdWithFilters(any(), any(), any(), any(), any()))
                .thenReturn(page);
        when(transactionDetailRepository.findByMasterTxnIdInList(anyList()))
                .thenReturn(List.of(createMockDetail(1L, 1L)));
        when(transactionRepository.countByStatus(any(), any(), any()))
                .thenReturn(List.of(new StatusCountResult("completed", 1L)));
        when(transactionRepository.getTotalAmount(any(), any(), any(), any()))
                .thenReturn(new TotalAmountResult(new BigDecimal("150.00")));
        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(createMockMember(1L, "Global Payment Services")));
        when(memberRepository.findById(4L))
                .thenReturn(Optional.of(createMockMember(4L, "Visa Worldwide")));

        // Act
        Optional<TransactionListResponse> result = useCase.execute(request);

        // Assert
        assertTrue(result.isPresent());
        TransactionListResponse response = result.get();
        assertEquals(merchantId, response.merchantId());
        assertNotNull(response.summary());
        assertEquals(1, response.transactions().size());
        assertNotNull(response.pagination());
        assertEquals(1L, response.pagination().totalElements());
    }

    @Test
    void execute_WithNoTransactions_ReturnsEmptyResponse() {
        // Arrange
        String merchantId = "EMPTY-MERCHANT";
        TransactionListRequest request = new TransactionListRequest(
                merchantId, 0, 20, null, null, null
        );

        Page<TransactionMaster> emptyPage = Page.empty();
        
        when(transactionRepository.findByMerchantIdWithFilters(any(), any(), any(), any(), any()))
                .thenReturn(emptyPage);

        // Act
        Optional<TransactionListResponse> result = useCase.execute(request);

        // Assert
        assertTrue(result.isPresent());
        TransactionListResponse response = result.get();
        assertEquals(merchantId, response.merchantId());
        assertEquals(0L, response.summary().totalTransactions());
        assertTrue(response.transactions().isEmpty());
        assertEquals(0L, response.pagination().totalElements());
    }

    @Test
    void execute_WithDateFilters_PassesFiltersToRepository() {
        // Arrange
        String merchantId = "MCH-00001";
        Instant startDate = Instant.parse("2025-11-16T00:00:00Z");
        Instant endDate = Instant.parse("2025-11-18T23:59:59Z");
        TransactionListRequest request = new TransactionListRequest(
                merchantId, 0, 20, startDate, endDate, null
        );

        Page<TransactionMaster> emptyPage = Page.empty();
        when(transactionRepository.findByMerchantIdWithFilters(any(), any(), any(), any(), any()))
                .thenReturn(emptyPage);

        // Act
        useCase.execute(request);

        // Assert
        verify(transactionRepository).findByMerchantIdWithFilters(
                eq(merchantId),
                eq(startDate),
                eq(endDate),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    void execute_WithStatusFilter_PassesStatusToRepository() {
        // Arrange
        String merchantId = "MCH-00001";
        String status = "completed";
        TransactionListRequest request = new TransactionListRequest(
                merchantId, 0, 20, null, null, status
        );

        Page<TransactionMaster> emptyPage = Page.empty();
        when(transactionRepository.findByMerchantIdWithFilters(any(), any(), any(), any(), any()))
                .thenReturn(emptyPage);

        // Act
        useCase.execute(request);

        // Assert
        verify(transactionRepository).findByMerchantIdWithFilters(
                eq(merchantId),
                isNull(),
                isNull(),
                eq(status),
                any(Pageable.class)
        );
    }

    @Test
    void execute_WithMultipleTransactions_AggregatesDetailsCorrectly() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionListRequest request = new TransactionListRequest(
                merchantId, 0, 20, null, null, null
        );

        TransactionMaster txn1 = createMockTransaction(1L, merchantId);
        TransactionMaster txn2 = createMockTransaction(2L, merchantId);
        Page<TransactionMaster> page = Page.of(List.of(txn1, txn2), Pageable.from(0, 20), 2);
        
        when(transactionRepository.findByMerchantIdWithFilters(any(), any(), any(), any(), any()))
                .thenReturn(page);
        when(transactionDetailRepository.findByMasterTxnIdInList(anyList()))
                .thenReturn(List.of(
                        createMockDetail(1L, 1L),
                        createMockDetail(2L, 1L),
                        createMockDetail(3L, 2L)
                ));
        when(transactionRepository.countByStatus(any(), any(), any()))
                .thenReturn(List.of(new StatusCountResult("completed", 2L)));
        when(transactionRepository.getTotalAmount(any(), any(), any(), any()))
                .thenReturn(new TotalAmountResult(new BigDecimal("300.00")));
        when(memberRepository.findById(any()))
                .thenReturn(Optional.of(createMockMember(1L, "Test Member")));

        // Act
        Optional<TransactionListResponse> result = useCase.execute(request);

        // Assert
        assertTrue(result.isPresent());
        TransactionListResponse response = result.get();
        assertEquals(2, response.transactions().size());
        
        // Verify first transaction has 2 details
        TransactionDTO firstTxn = response.transactions().get(0);
        assertEquals(2, firstTxn.details().size());
        
        // Verify second transaction has 1 detail
        TransactionDTO secondTxn = response.transactions().get(1);
        assertEquals(1, secondTxn.details().size());
    }

    @Test
    void execute_CalculatesSummaryCorrectly() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionListRequest request = new TransactionListRequest(
                merchantId, 0, 20, null, null, null
        );

        TransactionMaster txn = createMockTransaction(1L, merchantId);
        Page<TransactionMaster> page = Page.of(List.of(txn), Pageable.from(0, 20), 1);
        
        when(transactionRepository.findByMerchantIdWithFilters(any(), any(), any(), any(), any()))
                .thenReturn(page);
        when(transactionDetailRepository.findByMasterTxnIdInList(anyList()))
                .thenReturn(List.of());
        when(transactionRepository.countByStatus(any(), any(), any()))
                .thenReturn(List.of(
                        new StatusCountResult("completed", 10L),
                        new StatusCountResult("pending", 5L),
                        new StatusCountResult("failed", 2L)
                ));
        when(transactionRepository.getTotalAmount(any(), any(), any(), any()))
                .thenReturn(new TotalAmountResult(new BigDecimal("5000.00")));
        when(memberRepository.findById(any()))
                .thenReturn(Optional.of(createMockMember(1L, "Test Member")));

        // Act
        Optional<TransactionListResponse> result = useCase.execute(request);

        // Assert
        assertTrue(result.isPresent());
        TransactionSummaryDTO summary = result.get().summary();
        assertEquals(17L, summary.totalTransactions()); // 10 + 5 + 2
        assertEquals(new BigDecimal("5000.00"), summary.totalAmount());
        assertEquals(3, summary.byStatus().size());
        assertEquals(10L, summary.byStatus().get("completed"));
        assertEquals(5L, summary.byStatus().get("pending"));
        assertEquals(2L, summary.byStatus().get("failed"));
    }

    private TransactionMaster createMockTransaction(Long id, String merchantId) {
        TransactionMaster txn = new TransactionMaster();
        txn.setTxnId(id);
        txn.setMerchantId(merchantId);
        txn.setAmount(new BigDecimal("150.00"));
        txn.setCurrency("USD");
        txn.setStatus("completed");
        txn.setLocalTxnDateTime(Instant.parse("2025-11-18T14:32:15Z"));
        txn.setCardType("VISA");
        txn.setCardLast4("4242");
        txn.setGpAcquirerId(1L);
        txn.setGpIssuerId(4L);
        return txn;
    }

    private TransactionDetail createMockDetail(Long id, Long masterTxnId) {
        TransactionDetail detail = new TransactionDetail();
        detail.setTxnDetailId(id);
        detail.setMasterTxnId(masterTxnId);
        detail.setDetailType("fee");
        detail.setAmount(new BigDecimal("3.50"));
        detail.setDescription("Processing fee");
        return detail;
    }

    private Member createMockMember(Long id, String name) {
        Member member = new Member();
        member.setMemberId(id);
        member.setMemberName(name);
        member.setMemberType("acquirer");
        member.setMemberCode("TEST-" + id);
        return member;
    }
}
