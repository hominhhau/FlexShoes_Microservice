package com.microservice.order_service.mapper;

import com.microservice.order_service.dto.CustomerDto;
import com.microservice.order_service.dto.InvoiceDto;
import com.microservice.order_service.entity.Invoice;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InvoiceMapper {
    private final ModelMapper modelMapper;
    private final InvoiceDetailMapper invoiceDetailMapper;

    public InvoiceMapper(ModelMapper modelMapper, InvoiceDetailMapper invoiceDetailMapper) {
        this.modelMapper = modelMapper;
        this.invoiceDetailMapper = invoiceDetailMapper;
    }

    // Chuyển đổi từ DTO sang Entity
    public Invoice toEntity(InvoiceDto invoiceDTO) {
        return Optional.ofNullable(invoiceDTO)
                .map(dto -> modelMapper.map(dto, Invoice.class))
                .orElse(null);
    }

    // Chuyển đổi từ Entity sang DTO
    public InvoiceDto toDTO(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceDto dto = modelMapper.map(invoice, InvoiceDto.class);
        dto.setInvoiceId(invoice.getInvoiceId()); // Đảm bảo set lại invoiceId

        // Xử lý danh sách InvoiceDetails
        dto.setInvoiceDetails(Optional.ofNullable(invoice.getInvoiceDetails())
                .orElse(Collections.emptyList()) // Nếu null thì dùng danh sách rỗng
                .stream()
                .map(invoiceDetailMapper::toInvoiceDetailDto)
                .collect(Collectors.toList()));

        return dto;
    }

    public List<Invoice> toEntities(List<InvoiceDto> invoiceDTOs) {
        return Optional.ofNullable(invoiceDTOs)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceDto> toDTOs(List<Invoice> invoices) {
        return Optional.ofNullable(invoices)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Phương thức để ánh xạ thêm thông tin khách hàng
    public InvoiceDto toDTO(Invoice invoice, CustomerDto customer) {
        InvoiceDto dto = toDTO(invoice);
        if (dto != null) {
            dto.setCustomer(customer); // Gán thông tin khách hàng nếu DTO hợp lệ
        }
        return dto;
    }
}
