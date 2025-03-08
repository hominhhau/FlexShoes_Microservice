package com.microservice.order_service.mapper;

import com.flexshoes.flexshoesbackend.dto.InvoiceDto;
import com.flexshoes.flexshoesbackend.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InvoiceDetailMapper.class})
public interface InvoiceMapper {

    // Ánh xạ từ DTO sang Entity (Không có Customer)
    @Mapping(target = "invoiceDetails", source = "invoiceDetails")
    Invoice toEntity(InvoiceDto invoiceDTO);

    // Ánh xạ từ Entity sang DTO
    InvoiceDto toDTO(Invoice invoice);

    List<Invoice> toEntities(List<InvoiceDto> invoiceDTOs);

    List<InvoiceDto> toDTOs(List<Invoice> invoices);

    // Cập nhật entity từ DTO
    void updateEntityFromDto(InvoiceDto invoiceDTO, @MappingTarget Invoice invoice);
}