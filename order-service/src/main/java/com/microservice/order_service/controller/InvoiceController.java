package com.microservice.order_service.controller;

import com.microservice.order_service.dto.InvoiceDto;
import com.microservice.order_service.dto.MyAPIResponse;
import com.microservice.order_service.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/invoices")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
//	@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.POST, RequestMethod.DELETE}, allowedHeaders = {"Content-Type", "Authorization"})
public class InvoiceController {
	InvoiceService invoiceService;

	// Lấy tất cả hóa đơn
	@GetMapping
	public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
		return ResponseEntity.ok(invoiceService.getAllInvoice());
	}

	// Tạo hóa đơn mới
	@PostMapping("/add")
	public ResponseEntity<InvoiceDto> createInvoice(@Valid @RequestBody InvoiceDto invoiceDto) {
		return ResponseEntity.ok(invoiceService.saveInvoice(invoiceDto));
	}

	// Lấy hóa đơn theo ID
	@GetMapping("/findById/{id}")
	public MyAPIResponse<InvoiceDto> findByID(@PathVariable Integer id) {
		return MyAPIResponse
				.<InvoiceDto>builder().result(invoiceService.getInvoice(id)).build();
	}

	@GetMapping("/findByCustomerId/{customerId}")
	public ResponseEntity<List<InvoiceDto>> findByCustomerId(@PathVariable Long customerId) {
		return ResponseEntity.ok(invoiceService.getInvoicesByCustomerId(customerId));
	}
	// Cập nhật hóa đơn
	@PutMapping("/updateInvoice")
	public MyAPIResponse<Boolean> updateInvoice( @Valid @RequestBody InvoiceDto invoiceDto) {
		return MyAPIResponse.<Boolean>builder().result(invoiceService.updateInvoice(invoiceDto)).build();
	}

	@PutMapping("/update/{id}/status")
	public ResponseEntity<MyAPIResponse<Void>> updateOrderStatus(
			@PathVariable Integer id, @RequestBody Map<String, String> request) {
		// Lấy trạng thái đơn hàng từ request body.  Sử dụng getOrDefault để
		// cung cấp giá trị mặc định là "" nếu "orderStatus" không có trong request.
		String orderStatus = request.getOrDefault("orderStatus", "").trim();
		//Kiểm tra xem trạng thái đơn hàng có rỗng không
		if (orderStatus.isEmpty()) {
			// Nếu trạng thái đơn hàng rỗng, trả về phản hồi lỗi Bad Request (400).
			return ResponseEntity.badRequest().body(
					MyAPIResponse.<Void>builder()
							.message("Trạng thái đơn hàng không được rỗng")
							.build()
			);
		}

		// Gọi service để cập nhật trạng thái đơn hàng.
		Boolean isUpdated = invoiceService.updateOrderStatus(id, orderStatus);
		if (Boolean.TRUE.equals(isUpdated)) {
			// Nếu cập nhật thành công, trả về phản hồi OK (200) với body rỗng.
			return ResponseEntity.ok(
					MyAPIResponse.<Void>builder()
							.build()
			);
		}
		// Nếu cập nhật không thành công (ví dụ: không tìm thấy hóa đơn),
		// trả về phản hồi Bad Request (400) với thông báo lỗi.
		return ResponseEntity.badRequest().body(
				MyAPIResponse.<Void>builder()
						.message("Không thể cập nhật trạng thái hoặc không tìm thấy hóa đơn")
						.build()
		);
	}

	// Get total order count
	@GetMapping("/total")
	public ResponseEntity<Long> getTotalOrderCount(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		Map<String, String> params = new HashMap<>();
		if (startDate != null && endDate != null) {
			params.put("startDate", startDate);
			params.put("endDate", endDate);
		}
		return ResponseEntity.ok(invoiceService.getTotalOrderCount(params));
	}

	// Get total shipping orders
	@GetMapping("/shipping")
	public ResponseEntity<Long> getTotalShippingOrders(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		Map<String, String> params = new HashMap<>();
		if (startDate != null && endDate != null) {
			params.put("startDate", startDate);
			params.put("endDate", endDate);
		}
		return ResponseEntity.ok(invoiceService.getTotalShippingOrders(params));
	}

	// Get total amount
	@GetMapping("/total-amount")
	public ResponseEntity<Double> getTotalAmount(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		Map<String, String> params = new HashMap<>();
		if (startDate != null && endDate != null) {
			params.put("startDate", startDate);
			params.put("endDate", endDate);
		}
		return ResponseEntity.ok(invoiceService.getTotalAmount(params));
	}

	// Get order count by month in a year
	@GetMapping("/stats/orders-by-month/{year}")
	public ResponseEntity<List<Map<String, Object>>> getOrderCountByMonthsInYear(
			@PathVariable int year,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		Map<String, String> params = new HashMap<>();
		if (startDate != null && endDate != null) {
			params.put("startDate", startDate);
			params.put("endDate", endDate);
		}
		return ResponseEntity.ok(invoiceService.getOrderCountByMonthsInYear(year, params));
	}

	// Get revenue by month in a year
	@GetMapping("/stats/revenue-by-month/{year}")
	public ResponseEntity<List<Map<String, Object>>> getRevenueByMonthsInYear(
			@PathVariable int year,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		Map<String, String> params = new HashMap<>();
		if (startDate != null && endDate != null) {
			params.put("startDate", startDate);
			params.put("endDate", endDate);
		}
		return ResponseEntity.ok(invoiceService.getRevenueByMonthsInYear(year, params));
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

	@GetMapping("/stats/orders-by-year")
	public ResponseEntity<List<Map<String, Object>>> getOrderCountByYears(
			@RequestParam String startDate,
			@RequestParam String endDate) {
		Map<String, String> params = new HashMap<>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		return ResponseEntity.ok(invoiceService.getOrderCountByYears(params));
	}

	@GetMapping("/stats/revenue-by-year")
	public ResponseEntity<List<Map<String, Object>>> getRevenueByYears(
			@RequestParam String startDate,
			@RequestParam String endDate) {
		Map<String, String> params = new HashMap<>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		return ResponseEntity.ok(invoiceService.getRevenueByYears(params));
	}

	@GetMapping("/stats/orders-by-day")
	public ResponseEntity<List<Map<String, Object>>> getOrderCountByDays(
			@RequestParam String startDate,
			@RequestParam String endDate) {
		Map<String, String> params = new HashMap<>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		return ResponseEntity.ok(invoiceService.getOrderCountByDays(params));
	}

	@GetMapping("/stats/revenue-by-day")
	public ResponseEntity<List<Map<String, Object>>> getRevenueByDays(
			@RequestParam String startDate,
			@RequestParam String endDate) {
		Map<String, String> params = new HashMap<>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		return ResponseEntity.ok(invoiceService.getRevenueByDays(params));
	}

}

