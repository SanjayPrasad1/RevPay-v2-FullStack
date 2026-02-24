package com.revpay.dto.request;

import com.revpay.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String securityQuestion;
    private String securityAnswer;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    // Business fields (required if accountType = BUSINESS)
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
}
