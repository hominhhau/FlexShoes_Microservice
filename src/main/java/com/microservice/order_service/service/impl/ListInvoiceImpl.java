package com.microservice.order_service.service.impl;


import com.microservice.order_service.entity.Invoice;
import com.microservice.order_service.repository.ListInvoiceRepository;
import com.microservice.order_service.service.ListInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ListInvoiceImpl implements ListInvoiceService {

    private final ListInvoiceRepository listInvoiceRepository;

    @Autowired
    public ListInvoiceImpl(ListInvoiceRepository listInvoiceRepository) {
        this.listInvoiceRepository = listInvoiceRepository;
    }

    @Override
    public List<Invoice> getInvoicesByCustomerId(Integer customerId) {
        return List.of();
    }
//
//    @Override
//    public List<Invoice> getInvoicesByCustomerId(Integer customerId) {
//        if (customerId == null) {
//            throw new IllegalArgumentException("Customer ID cannot be null");
//        }
//
//        List<Invoice> invoices = listInvoiceRepository.findByCustomerCustomerId(customerId);
//        return invoices != null ? invoices : Collections.emptyList();
//    }
}
