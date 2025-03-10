package com.microservice.order_service.controller;

import com.microservice.order_service.dto.InvoiceDto;
import com.microservice.order_service.dto.ProductDto;
import com.microservice.order_service.service.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/invoices")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InvoiceController {
	InvoiceService invoiceService;

	@GetMapping
	public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
		return ResponseEntity.ok(invoiceService.getAllInvoice());
	}

	@PostMapping("/add")
	public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceDto invoiceDto) {
		return ResponseEntity.ok(invoiceService.saveInvoice(invoiceDto));
	}

	@GetMapping("/product/{productId}")
	public ResponseEntity<ProductDto> getProductInfo(@PathVariable Integer productId) {
		return ResponseEntity.ok(invoiceService.getProductInfo(productId));
	}
}
