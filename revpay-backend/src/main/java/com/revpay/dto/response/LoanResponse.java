package com.revpay.dto.response;

import com.revpay.enums.LoanStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LoanResponse {
    private Long id;
    private String loanNumber;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private String purpose;
    private Integer tenure;
    private BigDecimal interestRate;
    private BigDecimal emi;
    private BigDecimal repaidAmount;
    private BigDecimal outstandingAmount;
    private String businessRevenue;
    private LoanStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}
