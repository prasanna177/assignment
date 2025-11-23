package com.payment.controller;
import com.payment.payload.merchants.MerchantAddPayload;
import com.payment.payload.merchants.MerchantDetailPayload;
import com.payment.payload.merchants.MerchantDetailResponse;
import com.payment.payload.merchants.MerchantEditPayload;
import com.payment.usecase.MerchantAddUseCase;
import com.payment.usecase.MerchantDetailUseCase;
import com.payment.usecase.MerchantEditUseCase;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import com.payment.RestResponse;
import com.payment.payload.SearchRequestPayload;
import com.payment.usecase.MerchantListUseCase;

import java.util.Optional;


@Controller("/api/v1/merchants")
public class MerchantController {

    private final MerchantListUseCase merchantListUseCase;
    private final MerchantAddUseCase merchantAddUseCase;
    private  final MerchantEditUseCase merchantEditUseCase;
    private final MerchantDetailUseCase merchantDetailUseCase;

    @Inject
    public MerchantController(MerchantListUseCase merchantUseCase,MerchantAddUseCase merchantAddUseCase, MerchantEditUseCase merchantEditUseCase, MerchantDetailUseCase merchantDetailUseCase) {
        this.merchantListUseCase = merchantUseCase;
        this.merchantAddUseCase = merchantAddUseCase;
        this.merchantEditUseCase = merchantEditUseCase;
        this.merchantDetailUseCase = merchantDetailUseCase;
    }

    @Post("/search")
    public HttpResponse<RestResponse> getMerchants(@Body SearchRequestPayload payload) {
        var response = this.merchantListUseCase.execute(payload);
        if (response.isEmpty()) {
            return HttpResponse.badRequest(RestResponse.error());
        }
        return HttpResponse.ok(RestResponse.success(response.get()));
    }

    @Post
    public HttpResponse<RestResponse> addMerchant(@Body MerchantAddPayload request) {
        var response = this.merchantAddUseCase.execute(request);
        if (response.isEmpty()) {
            return HttpResponse.badRequest(RestResponse.error());
        }
        return HttpResponse.ok(RestResponse.success(response.get()));
    }

    @Put
    public HttpResponse<RestResponse> updateMerchant(@Body MerchantEditPayload request) {
        var response = this.merchantEditUseCase.execute(request);
        if (response.isEmpty()) {
            return HttpResponse.badRequest(RestResponse.error());
        }
        return HttpResponse.ok(RestResponse.success(response.get()));
    }

    @Get("/{id}")
    public HttpResponse<RestResponse> getMerchantById(@QueryValue String id) {
        if (id == null) {
            return HttpResponse.badRequest(RestResponse.error());
        }
        MerchantDetailPayload payload = new MerchantDetailPayload(id);
        Optional<MerchantDetailResponse> response = this.merchantDetailUseCase.execute(payload);
        return HttpResponse.ok(RestResponse.success(response.get()));
    }
}

