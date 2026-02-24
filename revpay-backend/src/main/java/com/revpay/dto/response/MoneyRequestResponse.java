package com.revpay.dto.response;

import com.revpay.enums.MoneyRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MoneyRequestResponse {
    private Long id;
    private String requesterName;
    private String requesterUsername;
    private String recipientName;
    private String recipientUsername;
    private BigDecimal amount;
    private String purpose;
    private MoneyRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
