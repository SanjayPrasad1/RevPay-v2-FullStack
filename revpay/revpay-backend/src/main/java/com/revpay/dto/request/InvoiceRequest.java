package com.revpay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    private String customerAddress;

    @NotEmpty(message = "At least one line item is required")
    private List<LineItemRequest> lineItems;

    private String paymentTerms;
    private LocalDate dueDate;
    private String notes;

    @Data
    public static class LineItemRequest {
        private String description;
        private Integer quantity;
        private Double unitPrice;
        private Double taxRate;
    }
}
