package com.payment.usecase;

import java.util.List;
import java.util.Optional;

import com.payment.entity.Merchant;
import com.payment.payload.SearchRequestPayload;
import com.payment.payload.SearchResponse;
import com.payment.payload.SearchResponseBuilder;
import com.payment.repository.MerchantRepositoryImpl;
import jakarta.inject.Inject;

public class MerchantListUseCase implements UseCase<SearchRequestPayload, SearchResponse> {
    private final MerchantRepositoryImpl merchantRepository;

    @Inject
    public MerchantListUseCase(MerchantRepositoryImpl merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public Optional<SearchResponse> execute(SearchRequestPayload payload) {
        int count = this.merchantRepository.countDynamicSearch(payload);
        List<Merchant> merchantList = this.merchantRepository.dynamicSearch(payload);
        int totalPage = (int) Math.ceil((double) count / (double) payload.pageSize());
        SearchResponse searchResponse = SearchResponseBuilder.<Merchant>builder()
                        .currentPage(payload.pageNumber())
                        .totalRecord(count)
                        .pageSize(payload.pageSize())
                        .totalPage(totalPage)
                        .data(merchantList)
                        .build();
        return Optional.of(searchResponse);
    }
}
