package com.microservice.order_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.order_service.client.CustomerServiceClient;
import com.microservice.order_service.client.ProductServiceClient;
import com.microservice.order_service.dto.*;
import com.microservice.order_service.entity.Invoice;
import com.microservice.order_service.entity.InvoiceDetail;
import com.microservice.order_service.mapper.InvoiceMapper;
import com.microservice.order_service.repository.InvoiceDetailRepository;
import com.microservice.order_service.repository.InvoiceRepository;
import com.microservice.order_service.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final InvoiceDetailRepository detailRepository;
	private final ProductServiceClient productServiceClient;
	private final CustomerServiceClient customerServiceClient;
	private final InvoiceMapper invoiceMapper;

	@Override
	public ProductDto getProductInfo(String productId) {
		try {
			ProductDto productDto = productServiceClient.getProductById(productId);
			System.out.println("Thông tin sản phẩm nhận được: " + productDto);
			return productDto;
		} catch (Exception e) {
			throw new RuntimeException("Không thể lấy thông tin sản phẩm từ service: " + e.getMessage());
		}
	}

	@Override
	public CustomerDto getCustomerInfo(Long customerId) {
		try {
			System.out.println("HI");
			System.out.println(customerServiceClient.getCustomerById(customerId));
			ResponseEntity<ApiResponse<CustomerDto>> cus = customerServiceClient.getCustomerById(customerId);
			return (CustomerDto) cus.getBody().getResponse();
		} catch (Exception e) {
			throw new RuntimeException("Không thể lấy thông tin khách hàng từ service: " + e.getMessage());
		}
	}
//	public CustomerDto getCustomerInfo(Long customerId) {
//		ResponseEntity<ApiResponse<CustomerDto>> response = customerServiceClient.getCustomerById(customerId);
//		return response.getBody() != null ? response.getBody().getResponse() : null;
//	}
	@Override
	public InvoiceDto saveInvoice(InvoiceDto invoiceDto) {
		// Lấy thông tin khách hàng (nếu service không chạy thì trả về data mẫu)
		CustomerDto customer = getCustomerInfo(invoiceDto.getCustomerId());
		System.out.println("Thông tin khách hàng: " + customer);
		if (customer == null) {
			throw new RuntimeException("Không tìm thấy khách hàng với ID: " + invoiceDto.getCustomerId());
		}

		// Chuyển đổi DTO thành entity
		Invoice invoice = invoiceMapper.toEntity(invoiceDto);
		invoice.setInvoiceId(null); // Đảm bảo tạo mới
		invoice.setCustomerId(customer.getUserID());

		List<InvoiceDetail> details = new ArrayList<>();

		// Kiểm tra danh sách chi tiết hóa đơn
		if (invoiceDto.getInvoiceDetails() != null) {
			for (InvoiceDetailDto detailDto : invoiceDto.getInvoiceDetails()) {
				// **Kiểm tra giá trị productId ngay lập tức**
				System.out.println("InvoiceDetailDto productId nhận được: " + detailDto.getProductId());

				// Gọi ProductService để lấy thông tin sản phẩm
				String productId = detailDto.getProductId();
				ProductDto product = null;
				if (productId != null && !productId.isEmpty()) {
					product = getProductInfo(productId);
					System.out.println("Thông tin sản phẩm: " + product);
					if (product == null) {
						throw new RuntimeException("Sản phẩm ID " + productId + " không tồn tại!");
					}
					// **Set the ProductDto to the InvoiceDetailDto**
					detailDto.setProduct(product);

					// Tạo chi tiết hóa đơn entity
					InvoiceDetail detail = new InvoiceDetail();
					detail.setProductId(productId);
					detail.setQuantity(detailDto.getQuantity());
					detail.setInvoice(invoice);
					details.add(detail);
				} else {
					System.err.println("Cảnh báo: productId trong InvoiceDetailDto là null hoặc rỗng.");
					continue;
				}
			}
		}

		invoice.setInvoiceDetails(details); // Gán danh sách chi tiết hóa đơn vào invoice

		// Lưu hóa đơn, Hibernate sẽ lưu luôn các chi tiết hóa đơn do cascade (assuming configured)
		Invoice savedInvoice = invoiceRepository.save(invoice);

		// Convert the saved Invoice entity back to DTO, including the populated InvoiceDetails
		InvoiceDto savedInvoiceDto = invoiceMapper.toDTO(savedInvoice, customer);

		// **Mapping lại InvoiceDetail entities sang DTOs**
		List<InvoiceDetailDto> savedDetailDtos = new ArrayList<>();
		if (savedInvoice.getInvoiceDetails() != null) {
			for (InvoiceDetail savedDetail : savedInvoice.getInvoiceDetails()) {
				InvoiceDetailDto detailDto = new InvoiceDetailDto();
				detailDto.setDetailId(savedDetail.getDetailId());
				detailDto.setInvoiceId(savedDetail.getInvoice().getInvoiceId());
				detailDto.setProductId(savedDetail.getProductId());
				detailDto.setQuantity(savedDetail.getQuantity());

				// **Lấy thông tin ProductDto từ detailDto (đã được set trước đó)**
				for (InvoiceDetailDto originalDetailDto : invoiceDto.getInvoiceDetails()) {
					if (originalDetailDto.getProductId().equals(savedDetail.getProductId())) {
						detailDto.setProduct(originalDetailDto.getProduct());
						break;
					}
				}
				savedDetailDtos.add(detailDto);
			}
		}
		savedInvoiceDto.setInvoiceDetails(savedDetailDtos);

		return savedInvoiceDto;
	}


	@Override
	public List<InvoiceDto> getInvoicesByCustomerId(Long customerId) {
		if (customerId == null) {
			throw new IllegalArgumentException("Customer ID không được để trống!");
		}

		List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);

		if (invoices == null || invoices.isEmpty()) {
			System.out.println("Không tìm thấy hóa đơn nào cho khách hàng có ID: " + customerId);
			return new ArrayList<>();
		}

		System.out.println("Tìm thấy " + invoices.size() + " hóa đơn cho khách hàng ID: " + customerId);

		return invoices.stream()
				.map(invoice -> {
					CustomerDto customerDto = getCustomerInfo(invoice.getCustomerId());
					InvoiceDto invoiceDto = invoiceMapper.toDTO(invoice, customerDto);
					List<InvoiceDetailDto> detailDtos = invoice.getInvoiceDetails().stream()
							.map(detail -> {
								InvoiceDetailDto detailDto = new InvoiceDetailDto();
								detailDto.setDetailId(detail.getDetailId()); // Set detailId
								detailDto.setInvoiceId(invoice.getInvoiceId()); // Set invoiceId
								detailDto.setProductId(detail.getProductId());
								detailDto.setQuantity(detail.getQuantity());
								// **Fetch Product information for each detail**
								ProductDto productDto = getProductInfo(detail.getProductId());
								detailDto.setProduct(productDto);
								return detailDto;
							})
							.collect(Collectors.toList());
					invoiceDto.setInvoiceDetails(detailDtos);
					return invoiceDto;
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<Map<String, Object>> getOrderCountByMonthsInYear(int year, Map<String, String> params) {
		List<Object[]> results;
		if (params.containsKey("startDate") && params.containsKey("endDate")) {
			LocalDate startDate = LocalDate.parse(params.get("startDate"));
			LocalDate endDate = LocalDate.parse(params.get("endDate"));
			if (startDate.isAfter(endDate)) {
				throw new IllegalArgumentException("Start date cannot be later than end date");
			}
			// Convert LocalDate to LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX); // 23:59:59.999999999
			results = invoiceRepository.countOrdersByMonthInRange(startDateTime, endDateTime);
		} else {
			results = invoiceRepository.countOrdersByMonthInYear(year);
		}

		List<Map<String, Object>> monthlyData = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			Map<String, Object> data = new HashMap<>();
			data.put("month", month);
			data.put("count", 0L);
			monthlyData.add(data);
		}

		for (Object[] result : results) {
			Integer month = (Integer) result[0];
			Long count = (Long) result[1];
			monthlyData.get(month - 1).put("count", count);
		}

		return monthlyData;
	}

	@Override
	public List<Map<String, Object>> getRevenueByMonthsInYear(int year, Map<String, String> params) {
		List<Object[]> results;
		if (params.containsKey("startDate") && params.containsKey("endDate")) {
			LocalDate startDate = LocalDate.parse(params.get("startDate"));
			LocalDate endDate = LocalDate.parse(params.get("endDate"));
			if (startDate.isAfter(endDate)) {
				throw new IllegalArgumentException("Start date cannot be later than end date");
			}
			// Convert LocalDate to LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
			results = invoiceRepository.sumRevenueByMonthInRange(startDateTime, endDateTime);
		} else {
			results = invoiceRepository.sumRevenueByMonthInYear(year);
		}

		List<Map<String, Object>> monthlyData = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			Map<String, Object> data = new HashMap<>();
			data.put("month", month);
			data.put("revenue", 0.0);
			monthlyData.add(data);
		}

		for (Object[] result : results) {
			Integer month = (Integer) result[0];
			Double revenue = (Double) result[1];
			monthlyData.get(month - 1).put("revenue", revenue != null ? revenue : 0.0);
		}

		return monthlyData;
	}
	@Override
	public List<InvoiceDto> getAllInvoice() {
		return invoiceRepository.findAll().stream()
				.map(invoiceMapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public InvoiceDto getInvoice(Integer id) {
		Invoice invoice = invoiceRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invoice với ID " + id + " không tồn tại"));
//		invoiceMapper.toDTO(invoice);
		InvoiceDto invoiceDto = invoiceMapper.toDTO(invoice);


		invoiceDto.getInvoiceDetails().forEach(
				invoiceDetailDto -> {
					ProductDto product = getProductInfo(invoiceDetailDto.getProductId());
					product.setProductId(invoiceDetailDto.getProductId());
					invoiceDetailDto.setProduct(product);
				}
		);

		return invoiceDto;
	}

	@Override
	public boolean updateOrderStatus(Integer invoiceId, String newStatus) {
		try {
			// Tìm hóa đơn theo ID.  Hàm findById trả về một Optional,
			// giúp xử lý trường hợp không tìm thấy hóa đơn một cách an toàn.
			Invoice invoice = invoiceRepository.findById(invoiceId)
					.orElse(null); // Nếu không tìm thấy, invoice sẽ là null.

			if (invoice == null) {
				// Nếu không tìm thấy hóa đơn, trả về false để chỉ ra rằng việc cập nhật không thành công.
				return false;
			}

			// Cập nhật trạng thái đơn hàng của hóa đơn. Loại bỏ khoảng trắng đầu và cuối.
			invoice.setOrderStatus(newStatus.trim());
			// Lưu các thay đổi vào cơ sở dữ liệu.
			invoiceRepository.save(invoice);
			// Trả về true để chỉ ra rằng việc cập nhật thành công.
			return true;
		} catch (Exception e) {
			// Xử lý mọi ngoại lệ có thể xảy ra trong quá trình cập nhật.
			// In ra stack trace để gỡ lỗi (nên ghi log thay vì in ra trong production).
			e.printStackTrace();
			// Trả về false nếu có lỗi xảy ra.
			return false;
		}
	}
	@Override
	public boolean updateInvoice(InvoiceDto invoiceDto) {
		try {
			Integer invoiceId = invoiceDto.getInvoiceId();
			Invoice invoice = invoiceRepository.findById(invoiceId)
					.orElseThrow(() -> new RuntimeException("Invoice không tồn tại"));

			// Cập nhật thông tin hóa đơn từ DTO
			invoice.setIssueDate(invoiceDto.getIssueDate());
			invoice.setReceiverNumber(invoiceDto.getReceiverNumber());
			invoice.setReceiverName(invoiceDto.getReceiverName());
			invoice.setReceiverAddress(invoiceDto.getReceiverAddress());
			invoice.setPaymentMethod(invoiceDto.getPaymentMethod());
			invoice.setDeliveryMethod(invoiceDto.getDeliveryMethod());
			invoice.setOrderStatus(invoiceDto.getOrderStatus());
			invoice.setTotal(invoiceDto.getTotal());

			// Cập nhật các InvoiceDetails
			if (invoiceDto.getInvoiceDetails() != null) {
				for (InvoiceDetailDto detailDTO : invoiceDto.getInvoiceDetails()) {
					InvoiceDetail invoiceDetail = detailRepository.findById(detailDTO.getDetailId())
							.orElseThrow(() -> new RuntimeException("InvoiceDetail không tồn tại"));

					invoiceDetail.setQuantity(detailDTO.getQuantity());
					invoiceDetail.setProductId(detailDTO.getProductId());
					invoiceDetail.setInvoice(invoice);

					detailRepository.save(invoiceDetail);
				}
			}
			invoiceRepository.save(invoice);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

//	@Override
//	public List<InvoiceDto> getRecentInvoices() {
//		return invoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "issueDate"))
//				.stream()
//				.map(invoiceMapper::toDTO)
//				.collect(Collectors.toList());
//	}

	@Override
	public List<InvoiceDto> getRecentInvoices() {
		return invoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "issueDate"))
				.stream()
				.map(invoice -> {
					InvoiceDto invoiceDto = invoiceMapper.toDTO(invoice);
					// Lấy thông tin khách hàng
					ResponseEntity<ApiResponse<CustomerDto>> customerResponse =
							customerServiceClient.getCustomerById(invoice.getCustomerId());
					if (customerResponse.getBody() != null && customerResponse.getBody().getResponse() != null) {
						CustomerDto customer = customerResponse.getBody().getResponse();
						invoiceDto.setCustomer(customer);
						// Tạo customerName từ firstName và lastName
						String customerName = (customer.getFirstName() != null ? customer.getFirstName() : "") + " " +
								(customer.getLastName() != null ? customer.getLastName() : "");
						invoiceDto.setCustomerName(customerName.trim());
					}
					return invoiceDto;
				})
				.collect(Collectors.toList());
	}

	@Override
	public long getTotalOrderCount(Map<String, String> params) {
		if (params.containsKey("startDate") && params.containsKey("endDate")) {
			LocalDate startDate = LocalDate.parse(params.get("startDate"));
			LocalDate endDate = LocalDate.parse(params.get("endDate"));
			if (startDate.isAfter(endDate)) {
				throw new IllegalArgumentException("Start date cannot be later than end date");
			}
			// Convert LocalDate to LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
			return invoiceRepository.countByIssueDateBetween(startDateTime, endDateTime);
		}
		return invoiceRepository.count();
	}

	@Override
	public long getTotalShippingOrders(Map<String, String> params) {
		if (params.containsKey("startDate") && params.containsKey("endDate")) {
			LocalDate startDate = LocalDate.parse(params.get("startDate"));
			LocalDate endDate = LocalDate.parse(params.get("endDate"));
			if (startDate.isAfter(endDate)) {
				throw new IllegalArgumentException("Start date cannot be later than end date");
			}
			// Convert LocalDate to LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
			return invoiceRepository.countByOrderStatusAndIssueDateBetween("Processing", startDateTime, endDateTime);
		}
		return invoiceRepository.countByOrderStatus("Processing");
	}

	@Override
	public double getTotalAmount(Map<String, String> params) {
		if (params.containsKey("startDate") && params.containsKey("endDate")) {
			LocalDate startDate = LocalDate.parse(params.get("startDate"));
			LocalDate endDate = LocalDate.parse(params.get("endDate"));
			if (startDate.isAfter(endDate)) {
				throw new IllegalArgumentException("Start date cannot be later than end date");
			}
			// Convert LocalDate to LocalDateTime
			LocalDateTime startDateTime = startDate.atStartOfDay();
			LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
			return invoiceRepository.sumTotalAmountByIssueDateBetween(startDateTime, endDateTime);
		}
		return invoiceRepository.sumTotalAmount();
	}

	// Danh sách khách hàng được cache trong OrderService
	private static final List<CustomerDto> customers = new ArrayList<>();

	// Hàm tìm customerId từ customerName trong danh sách cache
	private Long getCustomerIdByName(String customerName) {
		for (CustomerDto customer : customers) {
			if (customer.getLastName().equalsIgnoreCase(customerName)) {
				return customer.getProfileKey();
			}
		}
		return null; // Trả về null nếu không tìm thấy
	}

	@Override
	public InvoiceDto createInvoiceFormOrder(InvoiceDto invoiceDto) {
		Invoice invoice = invoiceMapper.toEntity(invoiceDto);
		Invoice savedInvoice = invoiceRepository.save(invoice);

		if (invoiceDto.getInvoiceDetails() != null) {
			for (InvoiceDetailDto detailDto : invoiceDto.getInvoiceDetails()) {
				ProductDto product = getProductInfo(detailDto.getProductId());
				InvoiceDetail detail = new InvoiceDetail();
				detail.setProductId(product.getProductId());
				detail.setQuantity(detailDto.getQuantity());
				detail.setInvoice(savedInvoice);
				detailRepository.save(detail);
			}
		}
		return invoiceMapper.toDTO(savedInvoice);
	}

	@Override
	public List<InvoiceDto> searchInvoices(Integer id, String customerName, String orderStatus) {
		// Lấy danh sách hóa đơn từ repository
		List<Invoice> invoices = invoiceRepository.searchInvoices(id, null, orderStatus);

		// Lọc theo customerName nếu có
		if (customerName != null && !customerName.trim().isEmpty()) {
			List<Invoice> filteredInvoices = new ArrayList<>();
			for (Invoice invoice : invoices) {
				try {
					ResponseEntity<ApiResponse<CustomerDto>> customerResponse =
							customerServiceClient.getCustomerById(invoice.getCustomerId());
					if (customerResponse.getBody() != null && customerResponse.getBody().getResponse() != null) {
						CustomerDto customer = customerResponse.getBody().getResponse();
						String fullName = (customer.getFirstName() != null ? customer.getFirstName() : "") + " " +
								(customer.getLastName() != null ? customer.getLastName() : "").trim();
						if (fullName.toLowerCase().contains(customerName.toLowerCase())) {
							filteredInvoices.add(invoice);
						}
					}
				} catch (Exception e) {
					System.err.println("Lỗi khi gọi customerServiceClient cho hóa đơn " + invoice.getInvoiceId() + ": " + e.getMessage());
				}
			}
			invoices = filteredInvoices;
		}

		// Chuyển đổi sang InvoiceDto
		return invoices.stream()
				.map(invoice -> {
					CustomerDto customerDto = getCustomerInfo(invoice.getCustomerId());
					InvoiceDto invoiceDto = invoiceMapper.toDTO(invoice, customerDto);
					// Gán customerName thủ công để đảm bảo không null
					if (customerDto != null) {
						String fullName = (customerDto.getFirstName() != null ? customerDto.getFirstName() : "") + " " +
								(customerDto.getLastName() != null ? customerDto.getLastName() : "").trim();
						invoiceDto.setCustomerName(fullName.isEmpty() ? "Khách hàng không xác định" : fullName);
					} else {
						invoiceDto.setCustomerName("Khách hàng không xác định");
					}
					// Gán invoiceDetails
					List<InvoiceDetailDto> detailDtos = invoice.getInvoiceDetails().stream()
							.map(detail -> {
								InvoiceDetailDto detailDto = new InvoiceDetailDto();
								detailDto.setDetailId(detail.getDetailId());
								detailDto.setInvoiceId(invoice.getInvoiceId());
								detailDto.setProductId(detail.getProductId());
								detailDto.setQuantity(detail.getQuantity());
								ProductDto productDto = getProductInfo(detail.getProductId());
								detailDto.setProduct(productDto);
								return detailDto;
							})
							.collect(Collectors.toList());
					invoiceDto.setInvoiceDetails(detailDtos);
					return invoiceDto;
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<Map<String, Object>> getOrderCountByYears(Map<String, String> params) {
		LocalDate startDate = LocalDate.parse(params.get("startDate"));
		LocalDate endDate = LocalDate.parse(params.get("endDate"));
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		List<Object[]> results = invoiceRepository.countOrdersByYears(startDateTime, endDateTime);

		List<Map<String, Object>> yearlyData = new ArrayList<>();
		int startYear = startDate.getYear();
		int endYear = endDate.getYear();
		for (int year = startYear; year <= endYear; year++) {
			Map<String, Object> data = new HashMap<>();
			data.put("year", year);
			data.put("count", 0L);
			yearlyData.add(data);
		}

		for (Object[] result : results) {
			Integer year = (Integer) result[0];
			Long count = (Long) result[1];
			yearlyData.stream()
					.filter(data -> data.get("year").equals(year))
					.findFirst()
					.ifPresent(data -> data.put("count", count));
		}

		return yearlyData;
	}

	@Override
	public List<Map<String, Object>> getRevenueByYears(Map<String, String> params) {
		LocalDate startDate = LocalDate.parse(params.get("startDate"));
		LocalDate endDate = LocalDate.parse(params.get("endDate"));
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		List<Object[]> results = invoiceRepository.sumRevenueByYears(startDateTime, endDateTime);

		List<Map<String, Object>> yearlyData = new ArrayList<>();
		int startYear = startDate.getYear();
		int endYear = endDate.getYear();
		for (int year = startYear; year <= endYear; year++) {
			Map<String, Object> data = new HashMap<>();
			data.put("year", year);
			data.put("revenue", 0.0);
			yearlyData.add(data);
		}

		for (Object[] result : results) {
			Integer year = (Integer) result[0];
			Double revenue = (Double) result[1];
			yearlyData.stream()
					.filter(data -> data.get("year").equals(year))
					.findFirst()
					.ifPresent(data -> data.put("revenue", revenue));
		}

		return yearlyData;
	}
	@Override
	public List<Map<String, Object>> getOrderCountByDays(Map<String, String> params) {
		LocalDate startDate = LocalDate.parse(params.get("startDate"));
		LocalDate endDate = LocalDate.parse(params.get("endDate"));
		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date cannot be later than end date");
		}
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		List<Object[]> results = invoiceRepository.countOrdersByDays(startDateTime, endDateTime);
		System.out.println("countOrdersByDays results: " + results);

		List<Map<String, Object>> dailyData = new ArrayList<>();
		LocalDate current = startDate;
		while (!current.isAfter(endDate)) {
			Map<String, Object> data = new HashMap<>();
			data.put("date", current.toString());
			data.put("count", 0L);
			dailyData.add(data);
			current = current.plusDays(1);
		}

		for (Object[] result : results) {
			String date = result[0].toString();
			// Xử lý count linh hoạt (Integer hoặc Long)
			Long count;
			if (result[1] instanceof Integer) {
				count = ((Integer) result[1]).longValue();
			} else {
				count = (Long) result[1];
			}
			dailyData.stream()
					.filter(data -> data.get("date").equals(date))
					.findFirst()
					.ifPresent(data -> data.put("count", count));
		}

		return dailyData;
	}

	@Override
	public List<Map<String, Object>> getRevenueByDays(Map<String, String> params) {
		LocalDate startDate = LocalDate.parse(params.get("startDate"));
		LocalDate endDate = LocalDate.parse(params.get("endDate"));
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		List<Object[]> results = invoiceRepository.sumRevenueByDays(startDateTime, endDateTime);

		List<Map<String, Object>> dailyData = new ArrayList<>();
		LocalDate current = startDate;
		while (!current.isAfter(endDate)) {
			Map<String, Object> data = new HashMap<>();
			data.put("date", current.toString());
			data.put("revenue", 0.0);
			dailyData.add(data);
			current = current.plusDays(1);
		}

		for (Object[] result : results) {
			String date = result[0].toString();
			Double revenue = (Double) result[1];
			dailyData.stream()
					.filter(data -> data.get("date").equals(date))
					.findFirst()
					.ifPresent(data -> data.put("revenue", revenue));
		}

		return dailyData;
	}


}
