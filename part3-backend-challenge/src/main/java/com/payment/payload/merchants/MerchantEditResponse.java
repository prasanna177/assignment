package com.payment.payload.merchants;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class MerchantEditResponse {
    private String id;

    public MerchantEditResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
