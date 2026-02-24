package com.revpay.controller;

import com.revpay.dto.request.MoneyRequestDTO;
import com.revpay.dto.response.ApiResponse;
import com.revpay.dto.response.MoneyRequestResponse;
import com.revpay.security.UserPrincipal;
import com.revpay.service.MoneyRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/money-requests")
@RequiredArgsConstructor
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<MoneyRequestResponse>> createRequest(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody MoneyRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Money request sent",
            moneyRequestService.createRequest(user.getId(), dto)));
    }

    @GetMapping("/incoming")
    public ResponseEntity<ApiResponse<List<MoneyRequestResponse>>> getIncoming(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(moneyRequestService.getIncomingRequests(user.getId())));
    }

    @GetMapping("/outgoing")
    public ResponseEntity<ApiResponse<List<MoneyRequestResponse>>> getOutgoing(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(moneyRequestService.getOutgoingRequests(user.getId())));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<MoneyRequestResponse>> acceptRequest(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @RequestParam(required = false) String pin) {
        return ResponseEntity.ok(ApiResponse.success("Request accepted",
            moneyRequestService.acceptRequest(user.getId(), id, pin)));
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<ApiResponse<MoneyRequestResponse>> declineRequest(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Request declined",
            moneyRequestService.declineRequest(user.getId(), id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<MoneyRequestResponse>> cancelRequest(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Request cancelled",
            moneyRequestService.cancelRequest(user.getId(), id)));
    }
}
