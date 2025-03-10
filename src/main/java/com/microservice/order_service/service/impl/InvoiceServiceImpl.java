package com.microservice.order_service.service.impl;

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
import org.springframework.stereotype.Service;

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

	@Override
	public ProductDto getProductInfo(Integer productId) {
		try {
			return productServiceClient.getProductById(productId);
		} catch (Exception e) {
			return ProductDto.builder()
					.productId(productId)
					.productName("Sản phẩm mẫu " + productId)
					.salePrice(100.0)
					.finalPrice(90.0)
					.status("DUMMY")
					.images(List.of("dummy-image.jpg"))
					.build();
		}
	}
	@Override
	public CustomerDto getCustomerInfo(Integer customerId) {
		try {
			return customerServiceClient.getCustomerById(customerId);
		} catch (Exception e) {
			return CustomerDto.builder()
					.customerId(customerId)
					.customerName("Khách hàng mẫu" + customerId)
					.email("dummy-customer@example.com")
					.phoneNumber("0343906000")
					.build();
		}
	}
	@Override
	public InvoiceDto saveInvoice(InvoiceDto invoiceDto) {
		// Lấy thông tin khách hàng (nếu service không chạy thì trả về data mẫu)
		CustomerDto customer = getCustomerInfo(invoiceDto.getCustomerId());
		if (customer == null) {
			throw new RuntimeException("Không tìm thấy khách hàng với ID: " + invoiceDto.getCustomerId());
		}

		// Chuyển đổi DTO thành entity
		Invoice invoice = invoiceMapper.toEntity(invoiceDto);
		invoice.setInvoiceId(null); // Đảm bảo tạo mới
		invoice.setCustomerId(customer.getCustomerId()); // Gán ID khách hàng vào hóa đơn

		List<InvoiceDetail> details = new ArrayList<>();

		// Kiểm tra danh sách chi tiết hóa đơn
		if (invoiceDto.getInvoiceDetails() != null) {
			for (InvoiceDetailDto detailDto : invoiceDto.getInvoiceDetails()) {
				// Gọi ProductService để lấy thông tin sản phẩm
				ProductDto product = getProductInfo(detailDto.getProductId());
				if (product == null) {
					throw new RuntimeException("Sản phẩm ID " + detailDto.getProductId() + " không tồn tại!");
				}

				// Tạo chi tiết hóa đơn
				InvoiceDetail detail = new InvoiceDetail();
				detail.setProductId(product.getProductId());
				detail.setQuantity(detailDto.getQuantity());
				detail.setInvoice(invoice); // Gán invoice vào detail

				details.add(detail);
			}
		}

		invoice.setInvoiceDetails(details); // Gán danh sách chi tiết hóa đơn vào invoice

		// Lưu hóa đơn, Hibernate sẽ lưu luôn các chi tiết hóa đơn do cascade
		Invoice savedInvoice = invoiceRepository.save(invoice);

		// Trả về DTO kèm theo thông tin khách hàng
		return invoiceMapper.toDTO(savedInvoice, customer);
	}

//@Override
//public InvoiceDto saveInvoice(InvoiceDto invoiceDto) {
//	// Lấy thông tin khách hàng (nếu service không chạy thì trả về data mẫu)
//	CustomerDto customer = getCustomerInfo(invoiceDto.getCustomerId());
//	if (customer == null) {
//		throw new RuntimeException("Không tìm thấy khách hàng với ID: " + invoiceDto.getCustomerId());
//	}
//
//	// Chuyển đổi DTO thành entity
//	Invoice invoice = invoiceMapper.toEntity(invoiceDto);
//	invoice.setInvoiceId(null); // Đảm bảo tạo mới
//	invoice.setCustomerId(customer.getCustomerId()); // Gán ID khách hàng vào hóa đơn
//
//	List<InvoiceDetail> details = new ArrayList<>();
//
//	// Kiểm tra danh sách chi tiết hóa đơn
//	if (invoiceDto.getInvoiceDetails() != null) {
//		for (InvoiceDetailDto detailDto : invoiceDto.getInvoiceDetails()) {
//			// Gọi ProductService để lấy thông tin sản phẩm
//			ProductDto product = getProductInfo(detailDto.getProductId());
//			if (product == null) {
//				throw new RuntimeException("Sản phẩm ID " + detailDto.getProductId() + " không tồn tại!");
//			}
//
//			// Tạo chi tiết hóa đơn
//			InvoiceDetail detail = new InvoiceDetail();
//			detail.setProductId(product.getProductId());
//			detail.setQuantity(detailDto.getQuantity());
//			detail.setInvoice(invoice); // Gán invoice vào detail
//
//			details.add(detail);
//		}
//	}
//
//	invoice.setInvoiceDetails(details); // Gán danh sách chi tiết hóa đơn vào invoice
//
//	// Lưu hóa đơn, Hibernate sẽ lưu luôn các chi tiết hóa đơn do cascade
//	Invoice savedInvoice = invoiceRepository.save(invoice);
//
//	return invoiceMapper.toDTO(savedInvoice);
//}


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
		return invoiceMapper.toDTO(invoice);
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
	private Integer getCustomerIdByName(String customerName) {
		for (CustomerDto customer : customers) {
			if (customer.getCustomerName().equalsIgnoreCase(customerName)) {
				return customer.getCustomerId();
			}
		}
		return null; // Trả về null nếu không tìm thấy
	}

	@Override
	public List<InvoiceDto> searchInvoices(Integer id, String customerName, String orderStatus) {
		Integer customerId = null;

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
