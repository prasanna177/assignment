package com.payment.usecase;

import com.payment.entity.Merchant;
import com.payment.payload.SearchRequestPayload;
import com.payment.payload.SearchRequestPayloadBuilder;
import com.payment.payload.merchants.MerchantAddPayload;
import com.payment.payload.merchants.MerchantAddResponse;
import com.payment.repository.MerchantRepository;
import com.payment.repository.MerchantRepositoryImpl;
import com.payment.sqlutils.QueryCriteria;
import com.payment.sqlutils.QueryCriteriaBuilder;
import com.payment.utils.HelperUtils;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

public class MerchantAddUseCase implements UseCase<MerchantAddPayload, MerchantAddResponse> {
    private final MerchantRepository merchantRepository;
    private final MerchantRepositoryImpl merchantRepositoryImpl;

    @Inject
    public MerchantAddUseCase(MerchantRepository merchantRepository, MerchantRepositoryImpl merchantRepositoryImpl) {
        this.merchantRepository = merchantRepository;
        this.merchantRepositoryImpl = merchantRepositoryImpl;
    }


    @Override
    public Optional<MerchantAddResponse> execute(MerchantAddPayload payload) {
        validateRequestPayload(payload);
        var response = this.merchantRepository.save(prepareMerchantEntityFromPayload(payload));
        var merchantAddResponse = new MerchantAddResponse(response.getId());
        return Optional.of(merchantAddResponse);
    }

    public void validateRequestPayload(MerchantAddPayload payload) {
        SearchRequestPayload searchRequestPayload = SearchRequestPayloadBuilder.builder()
                .pageNumber(0)
                .pageSize(0)
                .sortField(null)
                .sortOrder(null)
                .searchParameter(null)
                .build();
        List<Merchant> merchantList = this.merchantRepositoryImpl.dynamicSearch(searchRequestPayload);
        if (merchantList.stream().anyMatch(merchant -> merchant.getEmail().equalsIgnoreCase(payload.getEmail()))) {
            throw new IllegalArgumentException("Merchant email already exists");
        }
        if(HelperUtils.isBlankOrNull(payload.getName())) {
            throw new IllegalArgumentException("Name is required");
        }
        if (HelperUtils.isBlankOrNull(payload.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }
        if (HelperUtils.isBlankOrNull(payload.getPhone())) {
            throw new IllegalArgumentException("Phone is required");
        }
        if (HelperUtils.isBlankOrNull(payload.getStatus())) {
            throw new IllegalArgumentException("Status is required");
        }
        if (HelperUtils.isBlankOrNull(payload.getAddress())) {
            throw new IllegalArgumentException("Address is required");
        }
        if (HelperUtils.isBlankOrNull(payload.getBusinessName())) {
            throw new IllegalArgumentException("Business name is required");
        }

    }

    public Merchant prepareMerchantEntityFromPayload(MerchantAddPayload payload) {
        Merchant merchant = new Merchant();
        merchant.setId(HelperUtils.generateUUID());
        merchant.setName(payload.getName());
        merchant.setEmail(payload.getEmail());
        merchant.setPhone(payload.getPhone());
        merchant.setStatus(payload.getStatus());
        merchant.setAddress(payload.getAddress());
        merchant.setBusinessName(payload.getBusinessName());
        return merchant;
    }
}
