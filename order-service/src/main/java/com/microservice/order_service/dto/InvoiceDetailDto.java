package com.microservice.order_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDetailDto {
	Integer detailId;
	Integer invoiceId;
	Integer productId;
	int quantity;

	ProductDto product; // ✅ Thêm đối tượng ProductDto vào đây
}
