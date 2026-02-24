package com.revpay.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentMethodResponse {
    private Long id;
    private String cardHolderName;
    private String maskedCardNumber;
    private String lastFourDigits;
    private String expiryMonth;
    private String expiryYear;
    private String cardType;
    private String billingAddress;
    private boolean isDefault;
    private LocalDateTime createdAt;
}
