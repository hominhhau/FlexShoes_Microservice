package com.microservice.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDetailDto {
	Integer detailId;
		@NotNull(message = "Invoice ID là bắt buộc")
	Integer invoiceId;

		@NotNull(message = "Product ID là bắt buộc")
	String productId;

		@Min(value = 1, message = "Quantity phải lớn hơn 0")
	int quantity;
	ProductDto product;
}
