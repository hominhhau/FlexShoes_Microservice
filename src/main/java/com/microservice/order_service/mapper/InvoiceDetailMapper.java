package com.microservice.order_service.mapper;

import com.microservice.order_service.dto.InvoiceDetailDto;
import com.microservice.order_service.dto.ProductDto;
import com.microservice.order_service.entity.InvoiceDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InvoiceDetailMapper {

	@Mapping(target = "product", expression = "java(mapProduct(invoiceDetail.getProductId()))") // ✅ Ánh xạ toàn bộ ProductDto
	InvoiceDetailDto toInvoiceDetailDto(InvoiceDetail invoiceDetail);

	// Giả lập ánh xạ từ productId sang ProductDto
	default ProductDto mapProduct(Integer productId) {
		return ProductDto.builder()
				.productId(productId)
				.productName("Tên sản phẩm " + productId) // Giả lập tên sản phẩm
				.originalPrice(100.0) // Giả lập giá gốc
				.salePrice(90.0) // Giả lập giá bán
				.status("AVAILABLE") // Trạng thái giả lập
				.build();
	}
}
