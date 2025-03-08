package com.microservice.order_service.service.impl;

import com.flexshoes.flexshoesbackend.entity.Invoice;
import com.flexshoes.flexshoesbackend.repository.ListInvoiceRepository;
import com.flexshoes.flexshoesbackend.service.ListInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListInvoiceImpl implements ListInvoiceService {

    private final ListInvoiceRepository listInvoiceRepository;

    @Override
    public List<Invoice> getInvoicesByCustomerId(Integer customerId) {
        if (customerId == null) {
            log.error("Customer ID cannot be null");
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        List<Invoice> invoices = listInvoiceRepository.findByCustomerCustomerId(customerId);
        if (invoices == null || invoices.isEmpty()) {
            log.warn("No invoices found for customer ID: {}", customerId);
            return Collections.emptyList();
        }

        log.info("Found {} invoices for customer ID: {}", invoices.size(), customerId);
        return invoices;
    }
}
