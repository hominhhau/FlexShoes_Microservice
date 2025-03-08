package com.microservice.order_service.mapper;

import com.flexshoes.flexshoesbackend.dto.InvoiceDetailDto;
import com.flexshoes.flexshoesbackend.entity.InvoiceDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceDetailMapper {

	@Mapping(target = "invoiceId", source = "invoice.invoiceId")
	@Mapping(target = "productId", source = "productId")
	InvoiceDetailDto toDTO(InvoiceDetail detail);

	@Mapping(target = "invoice.invoiceId", source = "invoiceId")
	@Mapping(target = "productId", source = "productId")
	InvoiceDetail toEntity(InvoiceDetailDto detailDTO);

	List<InvoiceDetail> toEntities(List<InvoiceDetailDto> detailDTOs);

	List<InvoiceDetailDto> toDTOs(List<InvoiceDetail> details);
}
