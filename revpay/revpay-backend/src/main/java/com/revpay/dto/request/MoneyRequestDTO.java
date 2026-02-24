package com.revpay.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyRequestDTO {

    @NotBlank(message = "Recipient identifier is required")
    private String recipientIdentifier;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String purpose;
}
