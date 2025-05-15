package com.microservice.order_service.service;



import com.microservice.order_service.dto.CustomerDto;
import com.microservice.order_service.dto.InvoiceDto;
import com.microservice.order_service.dto.ProductDto;

import java.util.List;
import java.util.Map;

public interface InvoiceService {
    InvoiceDto createInvoiceFormOrder(InvoiceDto invoiceDto);

    List<InvoiceDto> getAllInvoice();

    InvoiceDto saveInvoice(InvoiceDto invoice);

    List<InvoiceDto> getRecentInvoices();

    long getTotalOrderCount();

    long getTotalShippingOrders();

    double getTotalAmount();

    InvoiceDto getInvoice(Integer id);

    boolean updateOrderStatus(Integer invoiceId, String newStatus);

    boolean updateInvoice(InvoiceDto invoiceDto);

    List<InvoiceDto> searchInvoices(Integer id, String customerName, String orderStatus);

    ProductDto getProductInfo(String productId);
    CustomerDto getCustomerInfo(Long customerId);
    List<InvoiceDto> getInvoicesByCustomerId(Long customerId);



    List<Map<String, Object>> getOrderCountByMonthsInYear(int year);
    List<Map<String, Object>> getRevenueByMonthsInYear(int year);
}
