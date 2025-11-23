package com.payment.controller;
import com.payment.payload.merchants.MerchantAddPayload;
import com.payment.payload.merchants.MerchantEditPayload;
import com.payment.usecase.MerchantAddUseCase;
import com.payment.usecase.MerchantEditUseCase;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import jakarta.inject.Inject;
import com.payment.RestResponse;
import com.payment.payload.SearchRequestPayload;
import com.payment.usecase.MerchantListUseCase;


@Controller("/api/v1/merchants")
public class MerchantController {

    private final MerchantListUseCase merchantListUseCase;
    private final MerchantAddUseCase merchantAddUseCase;
    private  final MerchantEditUseCase merchantEditUseCase;

    @Inject
    public MerchantController(MerchantListUseCase merchantUseCase,MerchantAddUseCase merchantAddUseCase, MerchantEditUseCase merchantEditUseCase) {
        this.merchantListUseCase = merchantUseCase;
        this.merchantAddUseCase = merchantAddUseCase;
        this.merchantEditUseCase = merchantEditUseCase;
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
}

