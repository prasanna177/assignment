package com.payment.payload;
import io.soabase.recordbuilder.core.RecordBuilder;


@RecordBuilder
public record MerchantInfo(
    String id,
    String name,
    String email,
    String phone,
    String status
){}
