package com.microservice.order_service.service;

import com.flexshoes.flexshoesbackend.entity.Invoice;

import java.util.List;

public interface ListInvoiceService {
    List<Invoice> getInvoicesByCustomerId(Integer customerId);
}
