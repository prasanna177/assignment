package com.payment.controller;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import com.payment.RestResponse;
import com.payment.payload.SearchRequestPayload;
import com.payment.usecase.MerchantUseCase;


@Controller("/api/v1/merchants")
public class MerchantController {

    private final MerchantUseCase merchantUseCase;

    @Inject
    public MerchantController(MerchantUseCase merchantUseCase) {
        this.merchantUseCase = merchantUseCase;
    }

    @Post
    public HttpResponse<RestResponse> getMerchants(@Body SearchRequestPayload payload) {
        var response = this.merchantUseCase.execute(payload);
        if (response.isEmpty()) {
            return HttpResponse.badRequest(RestResponse.error());
        }
        return HttpResponse.ok(RestResponse.success(response.get()));
    }
    }

