package com.revpay.controller;

import com.revpay.dto.request.LoanApplicationRequest;
import com.revpay.dto.response.ApiResponse;
import com.revpay.dto.response.LoanResponse;
import com.revpay.security.UserPrincipal;
import com.revpay.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LoanResponse>> applyForLoan(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Loan application submitted",
            loanService.applyForLoan(user.getId(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoans(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getLoans(user.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoan(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getLoan(user.getId(), id)));
    }

    @PostMapping("/{id}/repay")
    public ResponseEntity<ApiResponse<LoanResponse>> makeRepayment(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(ApiResponse.success("Repayment successful",
            loanService.makeRepayment(user.getId(), id, amount)));
    }
}
