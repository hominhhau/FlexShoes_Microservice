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
	public InvoiceDto saveInvoice(InvoiceDto invoiceDto) {
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
	public List<InvoiceDto> getRecentInvoices() {
		return List.of();
	}

	@Override
	public long getTotalOrderCount() {
		return 0;
	}

	@Override
	public long getTotalShippingOrders() {
		return 0;
	}

	@Override
	public double getTotalAmount() {
		return 0;
	}

	@Override
	public InvoiceDto getInvoice(Integer id) {
		return null;
	}

	@Override
	public Boolean updateOrderStatus(Integer invoiceId, String newStatus) {
		return null;
	}

	@Override
	public boolean updateInvoice(InvoiceDto invoiceDto) {
		return false;
	}

	@Override
	public List<InvoiceDto> searchInvoices(Integer id, String customerName, String orderStatus) {
		return List.of();
	}

	@Override
	public InvoiceDto createInvoiceFormOrder(InvoiceDto invoiceDto) {
		return null;
	}

	@Override
	public List<InvoiceDto> getAllInvoice() {
		return invoiceRepository.findAll().stream()
				.map(invoiceMapper::toDTO)
				.collect(Collectors.toList());
	}
}
