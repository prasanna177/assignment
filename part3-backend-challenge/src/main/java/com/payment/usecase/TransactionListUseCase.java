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
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Use case for listing transactions for a merchant
 */
@Singleton
public class TransactionListUseCase implements UseCase<TransactionListRequest, TransactionListResponse> {

    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final MemberRepository memberRepository;

    public TransactionListUseCase(
            TransactionRepository transactionRepository,
            TransactionDetailRepository transactionDetailRepository,
            MemberRepository memberRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Optional<TransactionListResponse> execute(TransactionListRequest request) {
        // Create pageable
        Pageable pageable = Pageable.from(request.page(), request.size());
        
        // Fetch transactions with filters
        Page<TransactionMaster> transactionPage = transactionRepository.findByMerchantIdWithFilters(
                request.merchantId(),
                request.startDate(),
                request.endDate(),
                request.status(),
                pageable
        );
        
        // If no transactions found, return empty response with zero summary
        if (transactionPage.isEmpty()) {
            return Optional.of(createEmptyResponse(request));
        }
        
        // Get transaction IDs for batch fetching details
        List<Long> txnIds = transactionPage.getContent().stream()
                .map(TransactionMaster::getTxnId)
                .toList();

        // Batch fetch transaction details
        List<TransactionDetail> allDetails = transactionDetailRepository.findByMasterTxnIdInList(txnIds);
        Map<Long, List<TransactionDetail>> detailsByTxnId = allDetails.stream()
                .collect(Collectors.groupingBy(TransactionDetail::getMasterTxnId));

        // Fetch all unique member IDs
        Set<Long> memberIds = new HashSet<>();
        transactionPage.getContent().forEach(txn -> {
            if (txn.getGpAcquirerId() != null) memberIds.add(txn.getGpAcquirerId());
            if (txn.getGpIssuerId() != null) memberIds.add(txn.getGpIssuerId());
        });

        // Batch fetch members
        Map<Long, Member> membersById = new HashMap<>();
        memberIds.forEach(id -> {
            memberRepository.findById(id).ifPresent(member -> 
                membersById.put(id, member)
            );
        });

        // Calculate summary
        TransactionSummaryDTO summary = calculateSummary(request);

        // Map to DTOs
        List<TransactionDTO> transactionDTOs = transactionPage.getContent().stream()
                .map(txn -> mapToDTO(txn, detailsByTxnId.getOrDefault(txn.getTxnId(), List.of()), membersById))
                .toList();

        // Create pagination DTO
        PaginationDTO pagination = new PaginationDTO(
                request.page(),
                request.size(),
                transactionPage.getTotalPages(),
                transactionPage.getTotalSize()
        );

        // Create date range DTO
        DateRangeDTO dateRange = new DateRangeDTO(
                request.startDate() != null ? request.startDate() : getEarliestDate(transactionPage),
                request.endDate() != null ? request.endDate() : getLatestDate(transactionPage)
        );

        return Optional.of(new TransactionListResponse(
                request.merchantId(),
                dateRange,
                summary,
                transactionDTOs,
                pagination
        ));
    }

    private TransactionListResponse createEmptyResponse(TransactionListRequest request) {
        TransactionSummaryDTO emptySummary = new TransactionSummaryDTO(
                0L,
                BigDecimal.ZERO,
                "USD",
                Map.of()
        );

        PaginationDTO emptyPagination = new PaginationDTO(
                request.page(),
                request.size(),
                0,
                0L
        );

        DateRangeDTO dateRange = new DateRangeDTO(
                request.startDate(),
                request.endDate()
        );

        return new TransactionListResponse(
                request.merchantId(),
                dateRange,
                emptySummary,
                List.of(),
                emptyPagination
        );
    }

    private TransactionSummaryDTO calculateSummary(TransactionListRequest request) {
        // Get status counts
        List<StatusCountResult> statusCounts = transactionRepository.countByStatus(
                request.merchantId(),
                request.startDate(),
                request.endDate()
        );

        Map<String, Long> byStatus = statusCounts.stream()
                .collect(Collectors.toMap(
                        StatusCountResult::getStatus,
                        StatusCountResult::getCount
                ));

        // Calculate total transactions
        long totalTransactions = byStatus.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        // Get total amount
        TotalAmountResult totalAmountResult = transactionRepository.getTotalAmount(
                request.merchantId(),
                request.startDate(),
                request.endDate(),
                request.status()
        );

        BigDecimal totalAmount = totalAmountResult != null && totalAmountResult.getTotalAmount() != null
                ? totalAmountResult.getTotalAmount()
                : BigDecimal.ZERO;

        return new TransactionSummaryDTO(
                totalTransactions,
                totalAmount,
                "USD",
                byStatus
        );
    }

    private TransactionDTO mapToDTO(
            TransactionMaster txn,
            List<TransactionDetail> details,
            Map<Long, Member> membersById
    ) {
        // Map transaction details
        List<TransactionDetailDTO> detailDTOs = details.stream()
                .map(detail -> new TransactionDetailDTO(
                        detail.getTxnDetailId(),
                        detail.getDetailType(),
                        detail.getAmount(),
                        detail.getDescription()
                ))
                .toList();

        // Get member names
        String acquirerName = txn.getGpAcquirerId() != null && membersById.containsKey(txn.getGpAcquirerId())
                ? membersById.get(txn.getGpAcquirerId()).getMemberName()
                : null;

        String issuerName = txn.getGpIssuerId() != null && membersById.containsKey(txn.getGpIssuerId())
                ? membersById.get(txn.getGpIssuerId()).getMemberName()
                : null;

        return new TransactionDTO(
                txn.getTxnId(),
                txn.getAmount(),
                txn.getCurrency(),
                txn.getStatus(),
                txn.getLocalTxnDateTime(),
                txn.getCardType(),
                txn.getCardLast4(),
                acquirerName,
                issuerName,
                detailDTOs
        );
    }

    private java.time.Instant getEarliestDate(Page<TransactionMaster> page) {
        return page.getContent().stream()
                .map(TransactionMaster::getLocalTxnDateTime)
                .min(java.time.Instant::compareTo)
                .orElse(null);
    }

    private java.time.Instant getLatestDate(Page<TransactionMaster> page) {
        return page.getContent().stream()
                .map(TransactionMaster::getLocalTxnDateTime)
                .max(java.time.Instant::compareTo)
                .orElse(null);
    }
}
