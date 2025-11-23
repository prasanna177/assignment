package com.payment.payload.merchants;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class MerchantAddResponse {
    private String id;

    public MerchantAddResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
