package com.microservice.order_service.mapper;

import com.microservice.order_service.dto.CustomerDto;
import com.microservice.order_service.dto.InvoiceDto;
import com.microservice.order_service.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InvoiceDetailMapper.class})
public interface InvoiceMapper {

    // Chuyển đổi từ DTO sang Entity
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "invoiceDetails", source = "invoiceDetails")
    Invoice toEntity(InvoiceDto invoiceDTO);

    // Chuyển đổi từ Entity sang DTO
    @Mapping(target = "customerId", source = "customerId")
    InvoiceDto toDTO(Invoice invoice);

    List<Invoice> toEntities(List<InvoiceDto> invoiceDTOs);

    List<InvoiceDto> toDTOs(List<Invoice> invoices);

    // Phương thức mặc định để ánh xạ thêm thông tin khách hàng
    default InvoiceDto toDTO(Invoice invoice, CustomerDto customer) {
        InvoiceDto dto = toDTO(invoice);
        dto.setCustomer(customer); // Gán thông tin khách hàng
        return dto;
    }
}
