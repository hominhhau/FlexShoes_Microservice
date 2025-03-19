package com.microservice.order_service.service;



import com.microservice.order_service.dto.CustomerDto;
import com.microservice.order_service.dto.InvoiceDto;
import com.microservice.order_service.dto.ProductDto;

import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoiceFormOrder(InvoiceDto invoiceDto);

    List<InvoiceDto> getAllInvoice();

    InvoiceDto saveInvoice(InvoiceDto invoice);

    List<InvoiceDto> getRecentInvoices();

    long getTotalOrderCount();

    long getTotalShippingOrders();

    double getTotalAmount();

    InvoiceDto getInvoice(Integer id);

    Boolean updateOrderStatus(Integer invoiceId, String newStatus);

    boolean updateInvoice(InvoiceDto invoiceDto);

    List<InvoiceDto> searchInvoices(Integer id, String customerName, String orderStatus);

    ProductDto getProductInfo(Integer productId);
    CustomerDto getCustomerInfo(Integer customerId);
}
