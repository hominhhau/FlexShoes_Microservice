package com.microservice.order_service.controller;


import com.flexshoes.flexshoesbackend.dto.InvoiceDto;
import com.flexshoes.flexshoesbackend.dto.response.MyAPIResponse;
import com.flexshoes.flexshoesbackend.service.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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

	// Lay all hoa don
	@GetMapping
	public List<InvoiceDto> getAllInvoices() {
		List<InvoiceDto> invoices = invoiceService.getAllInvoice();
		return new ResponseEntity<>(invoices, HttpStatus.OK).getBody();
	}


//	 Tạo hóa đơn mới
    @PostMapping("/add")
    public ResponseEntity<InvoiceDto> createInvoice1(@RequestBody InvoiceDto invoiceDto) {
        InvoiceDto invoice = invoiceService.saveInvoice(invoiceDto);
        return ResponseEntity.ok(invoice);
    }

	@PostMapping
	public ResponseEntity<Map<String, Object>> createInvoice(@RequestBody InvoiceDto invoiceDto) {
		InvoiceDto createdInvoice = invoiceService.createInvoiceFormOrder(invoiceDto);
		System.out.println("Generated Invoice ID: " + createdInvoice.getInvoiceId());
		Map<String, Object> response = new HashMap<>();
		response.put("id", createdInvoice.getInvoiceId());
		return ResponseEntity.ok(response);


	}

	@GetMapping("/recent")
	public ResponseEntity<List<InvoiceDto>> getRecentInvoices() {
		List<InvoiceDto> recentInvoices = invoiceService.getRecentInvoices();
		return ResponseEntity.ok(recentInvoices);
	}

	// Trả về tổng số đơn đặt hàng
	@GetMapping("/total")
	public ResponseEntity<Long> getTotalOrderCount() {
		long totalOrders = invoiceService.getTotalOrderCount();
		return ResponseEntity.ok(totalOrders);
	}

	// Trả về tổng số đơn đang vận chuyển (Processing)
	@GetMapping("/shipping")
	public ResponseEntity<Long> getTotalShippingOrders() {
		long totalShipping = invoiceService.getTotalShippingOrders();
		return ResponseEntity.ok(totalShipping);
	}

	// Trả về tổng số tiền của tất cả các hóa đơn
	@GetMapping("/total-amount")
	public ResponseEntity<Double> getTotalAmount() {
		double totalAmount = invoiceService.getTotalAmount();
		return ResponseEntity.ok(totalAmount);
	}

	@GetMapping("/findById/{id}")
	public MyAPIResponse<InvoiceDto> findByID(@PathVariable Integer id) {
		return MyAPIResponse.<InvoiceDto>builder().result(invoiceService.getInvoice(id)).build();
	}
	@PutMapping("/updateInvoice")
	public MyAPIResponse<Boolean> updateInvoice(@RequestBody InvoiceDto invoiceDto) {
		return MyAPIResponse.<Boolean>builder().result(invoiceService.updateInvoice(invoiceDto)).build();
	}
	@GetMapping("/search")
	public ResponseEntity<List<InvoiceDto>> searchInvoices(
			@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String customerName,
			@RequestParam(required = false) String orderStatus) {
		List<InvoiceDto> invoices = invoiceService.searchInvoices(id, customerName, orderStatus);
		return ResponseEntity.ok(invoices);
	}

}

