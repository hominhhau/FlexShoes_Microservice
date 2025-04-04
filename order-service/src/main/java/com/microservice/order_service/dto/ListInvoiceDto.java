package com.microservice.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListInvoiceDto {
    @NotNull(message = "Invoice ID là bắt buộc")
    Integer invoiceId;

    @NotNull(message = "Issue date là bắt buộc")
    LocalDate issueDate;

    @Min(value = 1, message = "Quantity phải lớn hơn 0")
    int quantity;

    @NotBlank(message = "Order status là bắt buộc")
    String orderStatus;

    @Min(value = 0, message = "Total phải lớn hơn hoặc bằng 0")
    double total;

    @NotNull(message = "Product ID là bắt buộc")
    Integer productId;

    ProductDto product; // Không bắt buộc, nên không thêm validation
}