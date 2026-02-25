package com.revpay.controller;

import com.revpay.dto.request.InvoiceRequest;
import com.revpay.dto.response.ApiResponse;
import com.revpay.dto.response.InvoiceResponse;
import com.revpay.enums.InvoiceStatus;
import com.revpay.security.UserPrincipal;
import com.revpay.service.InvoiceService;
import com.revpay.service.impl.InvoiceServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceServiceImpl invoiceServiceimpl;

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Invoice created",
            invoiceService.createInvoice(user.getId(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getInvoices(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            invoiceService.getInvoices(user.getId(), page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getInvoice(user.getId(), id)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateStatus(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
            invoiceService.updateStatus(user.getId(), id, status)));
    }

    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<ApiResponse<InvoiceResponse>> markAsPaid(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Invoice marked as paid",
            invoiceService.markAsPaid(user.getId(), id)));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<ApiResponse<Void>> sendInvoice(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        invoiceService.sendInvoice(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Invoice sent", null));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getReceivedInvoices(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceServiceimpl.getReceivedInvoices(user.getEmail(), page, size)));
    }
}
