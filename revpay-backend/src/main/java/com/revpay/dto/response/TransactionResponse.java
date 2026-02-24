package com.revpay.dto.response;

import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private String transactionId;
    private String senderName;
    private String senderUsername;
    private String receiverName;
    private String receiverUsername;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String note;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
