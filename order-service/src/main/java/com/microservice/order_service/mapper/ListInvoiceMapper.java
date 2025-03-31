package com.microservice.order_service.mapper;

import com.microservice.order_service.dto.ListInvoiceDto;
import com.microservice.order_service.dto.ProductDto;
import com.microservice.order_service.entity.Invoice;
import com.microservice.order_service.entity.InvoiceDetail;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ListInvoiceMapper {
    private final ModelMapper modelMapper;

    public ListInvoiceMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ListInvoiceDto toListInvoiceDto(InvoiceDetail invoiceDetail) {
        if (invoiceDetail == null || invoiceDetail.getInvoice() == null) {
            return null;
        }

        ListInvoiceDto dto = modelMapper.map(invoiceDetail, ListInvoiceDto.class);
        Invoice invoice = invoiceDetail.getInvoice();

        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setOrderStatus(invoice.getOrderStatus());
        dto.setTotal(invoice.getTotal());
        dto.setProduct(mapProductIdToProduct(invoiceDetail.getProductId()));

        return dto;
    }

    public List<ListInvoiceDto> toListInvoiceDtos(List<Invoice> invoices) {
        if (invoices == null || invoices.isEmpty()) {
            return Collections.emptyList();
        }

        return invoices.stream()
                .sorted((invoice1, invoice2) -> {
                    int dateComparison = Optional.ofNullable(invoice2.getIssueDate())
                            .map(date -> invoice1.getIssueDate() != null ? date.compareTo(invoice1.getIssueDate()) : 1)
                            .orElse(-1);
                    return (dateComparison != 0) ? dateComparison
                            : Optional.ofNullable(invoice2.getInvoiceId())
                            .map(id -> invoice1.getInvoiceId() != null ? id.compareTo(invoice1.getInvoiceId()) : 1)
                            .orElse(-1);
                })
                .flatMap(invoice -> Optional.ofNullable(invoice.getInvoiceDetails())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(this::toListInvoiceDto)
                        .filter(dto -> dto != null))
                .collect(Collectors.toList());
    }

    private ProductDto mapProductIdToProduct(Integer productId) {
        return (productId == null) ? null : ProductDto.builder()
                .productId(productId)
                .productName("Tên sản phẩm " + productId)
                .salePrice(0.0)
                .finalPrice(0.0)
                .status("UNKNOWN")
                .build();
    }
}

