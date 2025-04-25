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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final InvoiceDetailRepository detailRepository;
	private final ProductServiceClient productServiceClient;
	private final CustomerServiceClient customerServiceClient;
	private final InvoiceMapper invoiceMapper;

//	@Override
//	public ProductDto getProductInfo(String productId) {
//		try {
//			System.out.println("SP" + productServiceClient.getProductById(productId));
//			return productServiceClient.getProductById(productId);
//        } catch (Exception e) {
//			throw new RuntimeException("Không thể lấy thông tin sản phẩm từ service: " + e.getMessage());
//		}
//	}

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
//	@Override
//	public InvoiceDto saveInvoice(InvoiceDto invoiceDto) {
//		// Lấy thông tin khách hàng (nếu service không chạy thì trả về data mẫu)
//		CustomerDto customer = getCustomerInfo(invoiceDto.getCustomerId());
//		System.out.println(customer);
//		if (customer == null) {
//			throw new RuntimeException("Không tìm thấy khách hàng với ID: " + invoiceDto.getCustomerId());
//		}
//
//		// Chuyển đổi DTO thành entity
//		Invoice invoice = invoiceMapper.toEntity(invoiceDto);
//		invoice.setInvoiceId(null); // Đảm bảo tạo mới
//		invoice.setCustomerId(customer.getUserID());
//
//		List<InvoiceDetail> details = new ArrayList<>();
//
//		// Kiểm tra danh sách chi tiết hóa đơn
//		if (invoiceDto.getInvoiceDetails() != null) {
//			for (InvoiceDetailDto detailDto : invoiceDto.getInvoiceDetails()) {
//				// Gọi ProductService để lấy thông tin sản phẩm
//				ProductDto product = getProductInfo(detailDto.getProductId());
//				System.out.println("Product: " + product);
//				if (product == null) {
//					throw new RuntimeException("Sản phẩm ID " + detailDto.getProductId() + " không tồn tại!");
//				}
//
//				// Tạo chi tiết hóa đơn
//
//				InvoiceDetail detail = new InvoiceDetail();
//
//
//
//				detail.setProductId(detailDto.getProductId());
//				detail.setQuantity(detailDto.getQuantity());
//				detail.setInvoice(invoice); // Gán invoice vào detail
//
//				details.add(detail);
//			}
//		}
//
//		invoice.setInvoiceDetails(details); // Gán danh sách chi tiết hóa đơn vào invoice
//
//		// Lưu hóa đơn, Hibernate sẽ lưu luôn các chi tiết hóa đơn do cascade
//		Invoice savedInvoice = invoiceRepository.save(invoice);
//
//		// Trả về DTO kèm theo thông tin khách hàng
//		return invoiceMapper.toDTO(savedInvoice, customer);
//	}
//	public InvoiceDto saveInvoice(InvoiceDto invoiceDto) {
//		// Lấy thông tin khách hàng (nếu service không chạy thì trả về data mẫu)
//		CustomerDto customer = getCustomerInfo(invoiceDto.getCustomerId());
//		System.out.println("Thông tin khách hàng: " + customer);
//		if (customer == null) {
//			throw new RuntimeException("Không tìm thấy khách hàng với ID: " + invoiceDto.getCustomerId());
//		}
//
//		// Chuyển đổi DTO thành entity
//		Invoice invoice = invoiceMapper.toEntity(invoiceDto);
//		invoice.setInvoiceId(null); // Đảm bảo tạo mới
//		invoice.setCustomerId(customer.getUserID());
//
//		List<InvoiceDetail> details = new ArrayList<>();
//
//		// Kiểm tra danh sách chi tiết hóa đơn
//		if (invoiceDto.getInvoiceDetails() != null) {
//			for (InvoiceDetailDto detailDto : invoiceDto.getInvoiceDetails()) {
//				// **Kiểm tra giá trị productId ngay lập tức**
//				System.out.println("InvoiceDetailDto productId nhận được: " + detailDto.getProductId());
//
//				// Gọi ProductService để lấy thông tin sản phẩm
//				String productId = detailDto.getProductId();
//				ProductDto product = null;
//				if (productId != null && !productId.isEmpty()) {
//					product = getProductInfo(productId);
//					System.out.println("Thông tin sản phẩm: " + product);
//					if (product == null) {
//						throw new RuntimeException("Sản phẩm ID " + productId + " không tồn tại!");
//					}
//                    detailDto.setProduct(product);
//                    System.out.println("product khi taoh" + product);
//				} else {
//					System.err.println("Cảnh báo: productId trong InvoiceDetailDto là null hoặc rỗng.");
//					// **Tùy thuộc vào yêu cầu nghiệp vụ, bạn có thể xử lý khác ở đây:**
//					// - Bỏ qua chi tiết hóa đơn này
//					// - Gán một giá trị mặc định (nếu có logic cho nó)
//					// - Tiếp tục và có thể gây ra lỗi ở bước sau
//					// Trong ví dụ này, chúng ta sẽ không tạo InvoiceDetail nếu productId là null hoặc rỗng.
//					continue;
//				}
//
//				// Tạo chi tiết hóa đơn
//				InvoiceDetail detail = new InvoiceDetail();
//				detail.setProductId(productId);
//				detail.setQuantity(detailDto.getQuantity());
//				detail.setInvoice(invoice);
//
//				details.add(detail);
//			}
//		}
//
//		invoice.setInvoiceDetails(details); // Gán danh sách chi tiết hóa đơn vào invoice
//
//		// Lưu hóa đơn, Hibernate sẽ lưu luôn các chi tiết hóa đơn do cascade
//		Invoice savedInvoice = invoiceRepository.save(invoice);
//
//		// Trả về DTO kèm theo thông tin khách hàng
//		return invoiceMapper.toDTO(savedInvoice, customer);
//	}


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
	public Boolean updateOrderStatus(Integer invoiceId, String newStatus) {
		try {
			Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
			if (optionalInvoice.isPresent()) {
				Invoice invoice = optionalInvoice.get();
				invoice.setOrderStatus(newStatus);
				invoiceRepository.save(invoice);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
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

	@Override
	public List<InvoiceDto> getRecentInvoices() {
		return invoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "issueDate"))
				.stream()
				.map(invoiceMapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public long getTotalOrderCount() {
		return invoiceRepository.count();
	}

	@Override
	public long getTotalShippingOrders() {
		return invoiceRepository.countByOrderStatus("Processing");
	}

	@Override
	public double getTotalAmount() {
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
	public List<InvoiceDto> searchInvoices(Integer id, String customerName, String orderStatus) {
		Long customerId = null;

		// Nếu có tên khách hàng, tìm customerId trong danh sách cache
		if (customerName != null && !customerName.trim().isEmpty()) {
			customerId = getCustomerIdByName(customerName);
			if (customerId == null) {
				return List.of(); // Không tìm thấy khách hàng => Trả về danh sách rỗng
			}
		}

		// Tìm hóa đơn theo customerId tìm được
		return invoiceRepository.searchInvoices(id, customerId, orderStatus)
				.stream()
				.map(invoiceMapper::toDTO)
				.toList();
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
}
