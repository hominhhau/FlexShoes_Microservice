package com.microservice.order_service.service;


import com.microservice.order_service.entity.Invoice;

import java.util.List;

public interface ListInvoiceService {
    List<Invoice> getInvoicesByCustomerId(Integer customerId);
}
