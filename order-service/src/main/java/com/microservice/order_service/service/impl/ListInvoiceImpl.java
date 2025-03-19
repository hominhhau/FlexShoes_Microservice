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
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được để trống!");
        }

        List<Invoice> invoices = listInvoiceRepository.findByCustomerId(customerId);

        if (invoices == null || invoices.isEmpty()) {
            System.out.println("Không tìm thấy hóa đơn nào cho khách hàng có ID: " + customerId);
            return Collections.emptyList();
        }

        System.out.println("Tìm thấy " + invoices.size() + " hóa đơn cho khách hàng ID: " + customerId);
        return invoices;
    }
}
