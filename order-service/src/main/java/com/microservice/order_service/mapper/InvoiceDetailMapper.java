package com.microservice.order_service.mapper;

import com.microservice.order_service.dto.InvoiceDetailDto;
import com.microservice.order_service.dto.ProductDto;
import com.microservice.order_service.entity.InvoiceDetail;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class InvoiceDetailMapper {
	private final ModelMapper modelMapper;

	public InvoiceDetailMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
		configureMapping();
	}

	private void configureMapping() {
		modelMapper.typeMap(InvoiceDetail.class, InvoiceDetailDto.class).addMappings(mapper -> {
			mapper.map(src -> src.getInvoice().getInvoiceId(), InvoiceDetailDto::setInvoiceId);
		});
	}

	public InvoiceDetailDto toInvoiceDetailDto(InvoiceDetail invoiceDetail) {
		InvoiceDetailDto dto = modelMapper.map(invoiceDetail, InvoiceDetailDto.class);
		dto.setProduct(mapProduct(invoiceDetail.getProductId())); // Ánh xạ ProductDto từ productId
		return dto;
	}

	private ProductDto mapProduct(Integer productId) {
		return ProductDto.builder()
				.productId(productId)
				.productName("Tên sản phẩm " + productId) // Giả lập tên sản phẩm
				.originalPrice(100.0) // Giả lập giá gốc
				.salePrice(90.0) // Giả lập giá bán
				.status("AVAILABLE") // Trạng thái giả lập
				.build();
	}
}
