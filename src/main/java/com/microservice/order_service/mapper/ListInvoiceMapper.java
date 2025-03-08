package com.microservice.order_service.mapper;

import com.flexshoes.flexshoesbackend.dto.ListInvoiceDto;
import com.flexshoes.flexshoesbackend.entity.Invoice;

import java.util.List;
import java.util.stream.Collectors;

public class ListInvoiceMapper {

    public static List<ListInvoiceDto> toListInvoiceDto(List<Invoice> invoices) {
        return invoices.stream()
                // Sắp xếp theo ngày và id giảm dần
                .sorted((invoice1, invoice2) -> {
                    int dateComparison = invoice2.getIssueDate().compareTo(invoice1.getIssueDate());
                    if (dateComparison == 0) {
                        // Nếu ngày giống nhau, sắp xếp theo id giảm dần
                        return invoice2.getInvoiceId().compareTo(invoice1.getInvoiceId());
                    }
                    return dateComparison;
                })
                .flatMap(invoice -> invoice.getInvoiceDetails().stream().map(invoiceDetail -> {
                    ListInvoiceDto dto = new ListInvoiceDto();
                    dto.setIssueDate(invoice.getIssueDate());  // Ngày hóa đơn
                    dto.setInvoiceId(invoice.getInvoiceId());  // ID hóa đơn
                    dto.setQuantity(invoiceDetail.getQuantity());  // Số lượng sản phẩm
                    dto.setOrderStatus(invoice.getOrderStatus());  // Trạng thái đơn hàng
                    dto.setTotal(invoice.getTotal());  // Tổng tiền của hóa đơn

                    // Không có Product, lấy dữ liệu từ InvoiceDetail (nếu có)
                    dto.setProductId(invoiceDetail.getProductId()); // Giả sử có productId
                    dto.setProductName("Unknown Product");  // Đặt giá trị mặc định (hoặc gọi API lấy tên)
                    dto.setImage(null);  // Không có hình ảnh, để null

                    return dto;
                }))
                .collect(Collectors.toList());
    }
}
