package com.payment.repository;

import io.micronaut.core.annotation.Introspected;

/**
 * DTO for status count query results
 */
@Introspected
public class StatusCountResult {
    private String status;
    private Long count;

    public StatusCountResult() {
    }

    public StatusCountResult(String status, Long count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
