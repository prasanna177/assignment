package com.payment.usecase;

import jakarta.validation.Valid;

import java.util.Optional;

@FunctionalInterface
public interface UseCase <I,O> {
    Optional<O> execute(@Valid  I request);
}
