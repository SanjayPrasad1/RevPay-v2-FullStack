package com.revpay.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal walletBalance;
    private List<TransactionResponse> recentTransactions;
    private long pendingRequests;
    private long unreadNotifications;

    // Business analytics fields
    private BigDecimal totalReceived;
    private BigDecimal totalSent;
    private BigDecimal pendingInvoicesAmount;
    private BigDecimal paidInvoicesAmount;
    private long totalInvoices;
    private long activeLoans;
}
