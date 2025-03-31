package com.microservice.order_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListInvoiceDto {
    Integer invoiceId;
    LocalDate issueDate;
    int quantity;
    String orderStatus;
    double total;

    Integer productId; // Giữ lại productId nếu cần
    ProductDto product; // ✅ Thêm thông tin sản phẩm đầy đủ
}
