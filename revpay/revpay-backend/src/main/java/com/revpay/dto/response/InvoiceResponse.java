package com.revpay.dto.response;

import com.revpay.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private String businessName;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private List<LineItemResponse> lineItems;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String paymentTerms;
    private LocalDate dueDate;
    private String notes;
    private InvoiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    @Data
    @Builder
    public static class LineItemResponse {
        private Long id;
        private String description;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal taxRate;
        private BigDecimal lineTotal;
    }
}
