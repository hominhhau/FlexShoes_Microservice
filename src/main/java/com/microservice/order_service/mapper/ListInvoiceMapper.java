package com.microservice.order_service.mapper;

import com.microservice.order_service.dto.ListInvoiceDto;
import com.microservice.order_service.dto.ProductDto;
import com.microservice.order_service.entity.Invoice;
import com.microservice.order_service.entity.InvoiceDetail;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ListInvoiceMapper {

    @Mapping(target = "invoiceId", source = "invoice.invoiceId")
    @Mapping(target = "issueDate", source = "invoice.issueDate")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "orderStatus", source = "invoice.orderStatus")
    @Mapping(target = "total", source = "invoice.total")
    @Mapping(target = "product", source = "productId", qualifiedByName = "mapProductIdToProduct")
    ListInvoiceDto toListInvoiceDto(InvoiceDetail invoiceDetail);

    default List<ListInvoiceDto> toListInvoiceDtos(List<Invoice> invoices) {
        return invoices.stream()
                .sorted((invoice1, invoice2) -> {
                    int dateComparison = invoice2.getIssueDate().compareTo(invoice1.getIssueDate());
                    if (dateComparison == 0) {
                        return invoice2.getInvoiceId().compareTo(invoice1.getInvoiceId());
                    }
                    return dateComparison;
                })
                .flatMap(invoice -> invoice.getInvoiceDetails().stream()
                        .map(this::toListInvoiceDto))
                .collect(Collectors.toList());
    }

    @Named("mapProductIdToProduct")
    static ProductDto mapProductIdToProduct(Integer productId) {
        return ProductDto.builder()
                .productId(productId)
                .productName("Tên sản phẩm " + productId)
                .salePrice(0.0)
                .finalPrice(0.0)
                .status("UNKNOWN")
                .build();
    }
}
