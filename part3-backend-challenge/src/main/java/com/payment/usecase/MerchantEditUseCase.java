package com.payment.usecase;

import com.payment.entity.Merchant;
import com.payment.exception.NotFoundException;
import com.payment.payload.SearchRequestPayload;
import com.payment.payload.SearchRequestPayloadBuilder;
import com.payment.payload.merchants.MerchantEditPayload;
import com.payment.payload.merchants.MerchantEditResponse;
import com.payment.repository.MerchantRepository;
import com.payment.repository.MerchantRepositoryImpl;
import com.payment.utils.HelperUtils;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

public class MerchantEditUseCase implements UseCase<MerchantEditPayload, MerchantEditResponse>{
    private final MerchantRepository merchantRepository;
    private final MerchantRepositoryImpl merchantRepositoryImpl;

    @Inject
    public MerchantEditUseCase(MerchantRepository merchantRepository, MerchantRepositoryImpl merchantRepositoryImpl) {
        this.merchantRepository = merchantRepository;
        this.merchantRepositoryImpl = merchantRepositoryImpl;
    }

    @Override
    public Optional<MerchantEditResponse> execute(MerchantEditPayload request) {
        validateRequestPayload(request);

        Merchant existing = merchantRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Merchant not found"));

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setStatus(request.getStatus());
        existing.setAddress(request.getAddress());
        existing.setBusinessName(request.getBusinessName());

        merchantRepository.update(existing); // ✅ force UPDATE

        return Optional.of(new MerchantEditResponse(existing.getId()));
    }


    public void validateRequestPayload(MerchantEditPayload payload) {
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
}
