package com.microservice.order_service.service;

import com.flexshoes.flexshoesbackend.dto.InvoiceDto;

import java.util.List;

public interface InvoiceService {

    InvoiceDto createInvoiceFormOrder(InvoiceDto invoiceDto);

    List<InvoiceDto> getAllInvoice();

    boolean updateOrderStatus(Integer invoiceId, String newStatus);

    InvoiceDto getInvoice(Integer id);

    List<InvoiceDto> getRecentInvoices();

    long getTotalOrderCount();

    long getTotalShippingOrders();

    double getTotalAmount();

    boolean updateInvoice(InvoiceDto invoiceDTO);

    List<InvoiceDto> searchInvoices(Integer id, String customerName, String orderStatus);

    InvoiceDto saveInvoice(InvoiceDto invoiceDto);
}