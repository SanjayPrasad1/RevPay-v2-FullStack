package com.revpay.dto.response;

import com.revpay.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private AccountType accountType;
    private BigDecimal walletBalance;
    private boolean verified;
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
    private LocalDateTime createdAt;
}
