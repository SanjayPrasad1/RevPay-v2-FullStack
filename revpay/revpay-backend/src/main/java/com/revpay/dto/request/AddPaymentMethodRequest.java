package com.revpay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddPaymentMethodRequest {

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "Expiry month is required")
    private String expiryMonth;

    @NotBlank(message = "Expiry year is required")
    private String expiryYear;

    @NotBlank(message = "CVV is required")
    private String cvv;

    @NotBlank(message = "Card type is required")
    private String cardType;

    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingZip;
    private String billingCountry;

    private boolean isDefault;
}
