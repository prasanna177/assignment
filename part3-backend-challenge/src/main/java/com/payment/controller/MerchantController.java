package com.payment.controller;
import com.payment.payload.merchants.MerchantAddPayload;
import com.payment.usecase.MerchantAddUseCase;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import com.payment.RestResponse;
import com.payment.payload.SearchRequestPayload;
import com.payment.usecase.MerchantListUseCase;


@Controller("/api/v1/merchants")
public class MerchantController {

    private final MerchantListUseCase merchantListUseCase;
    private final MerchantAddUseCase merchantAddUseCase;

    @Inject
    public MerchantController(MerchantListUseCase merchantUseCase,MerchantAddUseCase merchantAddUseCase) {
        this.merchantListUseCase = merchantUseCase;
        this.merchantAddUseCase = merchantAddUseCase;
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
}

