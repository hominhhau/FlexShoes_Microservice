package com.microservice.order_service.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListInvoiceDto {
    private int invoiceId;
    private LocalDate issueDate;
    private Integer productId; // Thêm productId
    private String productName;
    private String image;
    private int quantity;
    private double total;
    private String orderStatus;
}
