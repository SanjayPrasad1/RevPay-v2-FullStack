package com.revpay.service;

import com.revpay.dto.request.InvoiceRequest;
import com.revpay.dto.response.InvoiceResponse;
import com.revpay.enums.InvoiceStatus;
import org.springframework.data.domain.Page;

public interface InvoiceService {
    InvoiceResponse createInvoice(Long userId, InvoiceRequest request);
    Page<InvoiceResponse> getInvoices(Long userId, int page, int size);
    InvoiceResponse getInvoice(Long userId, Long invoiceId);
    InvoiceResponse updateStatus(Long userId, Long invoiceId, InvoiceStatus status);
    InvoiceResponse markAsPaid(Long userId, Long invoiceId);
    void sendInvoice(Long userId, Long invoiceId);
    Page<InvoiceResponse> getReceivedInvoices(String customerEmail, int page, int size);
}
