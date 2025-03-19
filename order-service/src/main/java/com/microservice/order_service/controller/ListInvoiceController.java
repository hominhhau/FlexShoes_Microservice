package com.microservice.order_service.controller;

import com.microservice.order_service.dto.ListInvoiceDto;
import com.microservice.order_service.entity.Invoice;
import com.microservice.order_service.mapper.ListInvoiceMapper;
import com.microservice.order_service.service.ListInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class ListInvoiceController {

    private final ListInvoiceService listInvoiceService;
    private final ListInvoiceMapper listInvoiceMapper;

    @Autowired
    public ListInvoiceController(ListInvoiceService listInvoiceService, ListInvoiceMapper listInvoiceMapper) {
        this.listInvoiceService = listInvoiceService;
        this.listInvoiceMapper = listInvoiceMapper;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getInvoicesByCustomer(@PathVariable Integer customerId) {
        List<Invoice> invoices = listInvoiceService.getInvoicesByCustomerId(customerId);

        if (invoices == null || invoices.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "No invoices found for this customer.",
                    "customerId", customerId
            ));
        }

        // Chuyển đổi danh sách hóa đơn thành DTO
        List<ListInvoiceDto> invoiceDtos = listInvoiceMapper.toListInvoiceDtos(invoices);

        return ResponseEntity.ok(invoiceDtos);
    }
}
