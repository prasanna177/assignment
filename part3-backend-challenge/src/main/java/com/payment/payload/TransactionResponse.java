package com.payment.payload;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.sql.Date;

@RecordBuilder
@Introspected
@Serdeable
public record TransactionResponse(
        Long txnId,
        String merchantId,
        Date date,
        BigDecimal amount,
        String cardType,
        String status
) {
}
