package com.revpay.controller;

import com.revpay.dto.request.AddPaymentMethodRequest;
import com.revpay.dto.response.ApiResponse;
import com.revpay.dto.response.PaymentMethodResponse;
import com.revpay.security.UserPrincipal;
import com.revpay.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> addPaymentMethod(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody AddPaymentMethodRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment method added",
            paymentMethodService.addPaymentMethod(user.getId(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getPaymentMethods(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(paymentMethodService.getPaymentMethods(user.getId())));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        paymentMethodService.setDefault(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Default payment method updated", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Payment method removed", null));
    }
}
