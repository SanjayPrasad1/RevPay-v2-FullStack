package com.revpay.controller;

import com.revpay.dto.request.SendMoneyRequest;
import com.revpay.dto.request.TransactionFilterRequest;
import com.revpay.dto.request.WalletOperationRequest;
import com.revpay.dto.response.ApiResponse;
import com.revpay.dto.response.TransactionResponse;
import com.revpay.security.UserPrincipal;
import com.revpay.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<TransactionResponse>> sendMoney(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody SendMoneyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Money sent successfully",
            transactionService.sendMoney(user.getId(), request)));
    }

    @PostMapping("/add-funds")
    public ResponseEntity<ApiResponse<TransactionResponse>> addFunds(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody WalletOperationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Funds added successfully",
            transactionService.addFunds(user.getId(), request)));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody WalletOperationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Withdrawal successful",
            transactionService.withdraw(user.getId(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute TransactionFilterRequest filter) {
        return ResponseEntity.ok(ApiResponse.success(
            transactionService.getTransactions(user.getId(), filter)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> searchTransactions(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.success(
            transactionService.searchTransactions(user.getId(), query)));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.success(
            transactionService.getTransaction(user.getId(), transactionId)));
    }

    @GetMapping("/export/csv")
    public void exportCsv(@AuthenticationPrincipal UserPrincipal user,
                          HttpServletResponse response) throws Exception {
        transactionService.exportToCsv(user.getId(), response);
    }
}
