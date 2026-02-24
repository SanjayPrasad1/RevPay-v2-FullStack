package com.revpay.dto.request;

import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionFilterRequest {
    private TransactionType type;
    private TransactionStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String searchQuery;
    private int page = 0;
    private int size = 20;
}
