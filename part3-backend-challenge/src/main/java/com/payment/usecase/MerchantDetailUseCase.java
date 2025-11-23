package com.payment.usecase;

import com.payment.entity.Merchant;
import com.payment.exception.NotFoundException;
import com.payment.payload.merchants.MerchantDetailPayload;
import com.payment.payload.merchants.MerchantDetailResponse;
import com.payment.repository.MerchantRepository;
import jakarta.inject.Inject;

import java.util.Optional;

public class MerchantDetailUseCase implements UseCase<MerchantDetailPayload, MerchantDetailResponse> {

    private final MerchantRepository merchantRepository;

    @Inject
    public MerchantDetailUseCase(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public Optional<MerchantDetailResponse> execute(MerchantDetailPayload request) {
        Merchant merchant = merchantRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Merchant not found"));
        MerchantDetailResponse response = toDetailResponse(merchant);
        return Optional.of(response);
    }

    public MerchantDetailResponse toDetailResponse(Merchant merchant) {
        MerchantDetailResponse response = new MerchantDetailResponse();
        response.setId(merchant.getId());
        response.setName(merchant.getName());
        response.setEmail(merchant.getEmail());
        response.setPhone(merchant.getPhone());
        response.setStatus(merchant.getStatus());
        response.setAddress(merchant.getAddress());
        response.setBusinessName(merchant.getBusinessName());
        return response;
    }
}
