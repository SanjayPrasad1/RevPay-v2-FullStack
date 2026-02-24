package com.revpay.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanApplicationRequest {

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000", message = "Minimum loan amount is 1000")
    private BigDecimal requestedAmount;

    @NotBlank(message = "Loan purpose is required")
    private String purpose;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Minimum tenure is 1 month")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private Integer tenure;

    private String businessRevenue;
    private String businessDescription;
}
