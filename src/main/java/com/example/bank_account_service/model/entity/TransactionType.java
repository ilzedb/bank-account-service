package com.example.bank_account_service.model.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    CREDIT("CREDIT", "Credit Transaction"),
    DEBIT("DEBIT", "Debit Transaction");

    private final String code;
    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }
}
