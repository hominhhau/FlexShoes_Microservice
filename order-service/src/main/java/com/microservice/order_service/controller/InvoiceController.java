package com.microservice.order_service.controller;

import com.microservice.order_service.dto.InvoiceDto;
import com.microservice.order_service.dto.MyAPIResponse;
import com.microservice.order_service.service.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


	@CrossOrigin(origins = "http://localhost:3000")
	@RestController
	@RequestMapping("/api/invoices")
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	public class InvoiceController {
		InvoiceService invoiceService;

		// Lấy tất cả hóa đơn
		@GetMapping
		public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
			return ResponseEntity.ok(invoiceService.getAllInvoice());
		}

		// Tạo hóa đơn mới
		@PostMapping("/add")
		public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceDto invoiceDto) {
			return ResponseEntity.ok(invoiceService.saveInvoice(invoiceDto));
		}

		// Lấy hóa đơn theo ID
		@GetMapping("/findById/{id}")
		public MyAPIResponse<InvoiceDto> findByID(@PathVariable Integer id) {
			return MyAPIResponse.<InvoiceDto>builder().result(invoiceService.getInvoice(id)).build();
		}

		// Cập nhật hóa đơn
		@PutMapping("/updateInvoice")
		public MyAPIResponse<Boolean> updateInvoice(@RequestBody InvoiceDto invoiceDto) {
			return MyAPIResponse.<Boolean>builder().result(invoiceService.updateInvoice(invoiceDto)).build();
		}

		// Trả về tổng số đơn đặt hàng
		@GetMapping("/total")
		public ResponseEntity<Long> getTotalOrderCount() {
			return ResponseEntity.ok(invoiceService.getTotalOrderCount());
		}

		// Trả về tổng số đơn đang vận chuyển
		@GetMapping("/shipping")
		public ResponseEntity<Long> getTotalShippingOrders() {
			return ResponseEntity.ok(invoiceService.getTotalShippingOrders());
		}

		// Trả về tổng số tiền của tất cả hóa đơn
		@GetMapping("/total-amount")
		public ResponseEntity<Double> getTotalAmount() {
			return ResponseEntity.ok(invoiceService.getTotalAmount());
		}

		// Lấy danh sách hóa đơn gần đây
		@GetMapping("/recent")
		public ResponseEntity<List<InvoiceDto>> getRecentInvoices() {
			return ResponseEntity.ok(invoiceService.getRecentInvoices());
		}

		// Tìm kiếm hóa đơn theo ID, tên khách hàng và trạng thái đơn hàng
		@GetMapping("/search")
		public ResponseEntity<List<InvoiceDto>> searchInvoices(
				@RequestParam(required = false) Integer id,
				@RequestParam(required = false) String customerName,
				@RequestParam(required = false) String orderStatus) {
			return ResponseEntity.ok(invoiceService.searchInvoices(id, customerName, orderStatus));
		}
	}

