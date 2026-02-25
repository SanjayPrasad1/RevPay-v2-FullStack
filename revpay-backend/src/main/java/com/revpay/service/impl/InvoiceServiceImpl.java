package com.revpay.service.impl;

import com.revpay.dto.request.InvoiceRequest;
import com.revpay.dto.response.InvoiceResponse;
import com.revpay.entity.Invoice;
import com.revpay.entity.InvoiceLineItem;
import com.revpay.entity.Notification;
import com.revpay.entity.User;
import com.revpay.enums.AccountType;
import com.revpay.enums.InvoiceStatus;
import com.revpay.enums.NotificationType;
import com.revpay.exception.BadRequestException;
import com.revpay.repository.InvoiceRepository;
import com.revpay.repository.NotificationRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.InvoiceService;
import com.revpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(Long userId, InvoiceRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        if (user.getAccountType() != AccountType.BUSINESS) {
            throw new BadRequestException("Only business accounts can create invoices");
        }

        Invoice invoice = Invoice.builder()
            .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .businessUser(user)
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .customerAddress(request.getCustomerAddress())
            .paymentTerms(request.getPaymentTerms())
            .dueDate(request.getDueDate())
            .notes(request.getNotes())
            .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceRequest.LineItemRequest li : request.getLineItems()) {
            BigDecimal unitPrice = BigDecimal.valueOf(li.getUnitPrice());
            BigDecimal taxRate = li.getTaxRate() != null ? BigDecimal.valueOf(li.getTaxRate()) : BigDecimal.ZERO;
            BigDecimal lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(li.getQuantity()));
            BigDecimal lineTax = lineSubtotal.multiply(taxRate).divide(BigDecimal.valueOf(100));
            BigDecimal lineTotal = lineSubtotal.add(lineTax);

            InvoiceLineItem item = InvoiceLineItem.builder()
                .invoice(invoice)
                .description(li.getDescription())
                .quantity(li.getQuantity())
                .unitPrice(unitPrice)
                .taxRate(taxRate)
                .lineTotal(lineTotal)
                .build();

            invoice.getLineItems().add(item);
            subtotal = subtotal.add(lineSubtotal);
            totalTax = totalTax.add(lineTax);
        }

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(totalTax);
        invoice.setTotalAmount(subtotal.add(totalTax));

        invoice = invoiceRepository.save(invoice);
        return mapToResponse(invoice);
    }

    @Override
    public Page<InvoiceResponse> getInvoices(Long userId, int page, int size) {
        User user = userRepository.getReferenceById(userId);
        return invoiceRepository.findByBusinessUserOrderByCreatedAtDesc(user, PageRequest.of(page, size))
            .map(this::mapToResponse);
    }

    @Override
    public InvoiceResponse getInvoice(Long userId, Long invoiceId) {
        User user = userRepository.getReferenceById(userId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new BadRequestException("Invoice not found"));
        if (!invoice.getBusinessUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }
        return mapToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse updateStatus(Long userId, Long invoiceId, InvoiceStatus status) {
        User user = userRepository.getReferenceById(userId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new BadRequestException("Invoice not found"));
        if (!invoice.getBusinessUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }
        invoice.setStatus(status);
        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public InvoiceResponse markAsPaid(Long userId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new BadRequestException("Invoice not found"));
        if (!invoice.getBusinessUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public void sendInvoice(Long userId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new BadRequestException("Invoice not found"));
        if (!invoice.getBusinessUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }
        invoice.setStatus(InvoiceStatus.SENT);
        invoiceRepository.save(invoice);

        userRepository.findByEmail(invoice.getCustomerEmail()).ifPresent(customer -> {
            Notification notification = Notification.builder()
                    .user(customer) // Link the notification to the found user
                    .title("New Invoice Received")
                    .message("You received an invoice of $" + invoice.getTotalAmount() + " from " + invoice.getBusinessUser().getBusinessName())
                    .type(NotificationType.INVOICE)
                    .referenceId(invoice.getInvoiceNumber())
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
        });

//        notificationService.createNotification(invoice.getBusinessUser(), "Invoice Sent",
//            "Invoice " + invoice.getInvoiceNumber() + " sent to " + invoice.getCustomerName(),
//            NotificationType.INVOICE, String.valueOf(invoice.getId()));
    }
    @Override
    public Page<InvoiceResponse> getReceivedInvoices(String customerEmail, int page, int size){
        return invoiceRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail, PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    private InvoiceResponse mapToResponse(Invoice inv) {
        List<InvoiceResponse.LineItemResponse> items = inv.getLineItems().stream()
            .map(li -> InvoiceResponse.LineItemResponse.builder()
                .id(li.getId())
                .description(li.getDescription())
                .quantity(li.getQuantity())
                .unitPrice(li.getUnitPrice())
                .taxRate(li.getTaxRate())
                .lineTotal(li.getLineTotal())
                .build()).toList();

        return InvoiceResponse.builder()
            .id(inv.getId())
            .invoiceNumber(inv.getInvoiceNumber())
            .businessName(inv.getBusinessUser().getBusinessName())
            .customerName(inv.getCustomerName())
            .customerEmail(inv.getCustomerEmail())
            .customerAddress(inv.getCustomerAddress())
            .lineItems(items)
            .subtotal(inv.getSubtotal())
            .taxAmount(inv.getTaxAmount())
            .totalAmount(inv.getTotalAmount())
            .paymentTerms(inv.getPaymentTerms())
            .dueDate(inv.getDueDate())
            .notes(inv.getNotes())
            .status(inv.getStatus())
            .createdAt(inv.getCreatedAt())
            .paidAt(inv.getPaidAt())
            .build();
    }
}
