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




		return dto;
	}


}
