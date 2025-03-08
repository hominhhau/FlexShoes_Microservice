package com.microservice.order_service.service.impl;

import com.flexshoes.flexshoesbackend.client.ProductClient;
import com.flexshoes.flexshoesbackend.dto.InvoiceDto;
import com.flexshoes.flexshoesbackend.dto.ProductDto;
import com.flexshoes.flexshoesbackend.entity.Invoice;
import com.flexshoes.flexshoesbackend.entity.InvoiceDetail;
import com.flexshoes.flexshoesbackend.mapper.InvoiceMapper;
import com.flexshoes.flexshoesbackend.repository.InvoiceDetailRepository;
import com.flexshoes.flexshoesbackend.repository.InvoiceRepository;
import com.flexshoes.flexshoesbackend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final InvoiceDetailRepository detailRepository;
	private final ProductClient productClient;
	private final InvoiceMapper invoiceMapper;

	@Override
	public InvoiceDto createInvoiceFormOrder(InvoiceDto invoiceDto) {
		Invoice invoice = invoiceMapper.toEntity(invoiceDto);
		Invoice savedInvoice = invoiceRepository.save(invoice);

		if (invoice.getInvoiceDetails() != null) {
			invoice.getInvoiceDetails().forEach(detail -> {
				ProductDto product = productClient.getProductById(detail.getProductId());
				if (product == null) {
					throw new RuntimeException("Product not found with ID: " + detail.getProductId());
				}
				detail.setInvoice(savedInvoice);
			});
		}

		return invoiceMapper.toDTO(invoiceRepository.save(invoice));
	}

	@Override
	public List<InvoiceDto> getAllInvoice() {
		return invoiceRepository.findAll().stream()
				.map(invoiceMapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public boolean updateOrderStatus(Integer invoiceId, String newStatus) {
		return invoiceRepository.findById(invoiceId)
				.map(invoice -> {
					invoice.setOrderStatus(newStatus);
					invoiceRepository.save(invoice);
					return true;
				})
				.orElse(false);
	}

	@Override
	public InvoiceDto getInvoice(Integer id) {
		Invoice invoice = invoiceRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invoice with ID " + id + " not found"));
		return invoiceMapper.toDTO(invoice);
	}

	@Override
	public List<InvoiceDto> getRecentInvoices() {
		return invoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "issueDate"))
				.stream()
				.map(invoiceMapper::toDTO)
				.toList();
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

	@Override
	public boolean updateInvoice(InvoiceDto invoiceDTO) {
		Invoice invoice = invoiceRepository.findById(invoiceDTO.getInvoiceId())
				.orElseThrow(() -> new RuntimeException("Invoice not found"));

		invoiceMapper.updateEntityFromDto(invoiceDTO, invoice);
		invoiceRepository.save(invoice);

		if (invoiceDTO.getInvoiceDetails() != null) {
			invoiceDTO.getInvoiceDetails().forEach(detailDTO -> {
				InvoiceDetail invoiceDetail = detailRepository.findById(detailDTO.getDetailId())
						.orElseThrow(() -> new RuntimeException("InvoiceDetail not found"));

				invoiceDetail.setQuantity(detailDTO.getQuantity());
				detailRepository.save(invoiceDetail);
			});
		}

		return true;
	}

	@Override
	public List<InvoiceDto> searchInvoices(Integer id, String customerName, String orderStatus) {
		return invoiceRepository.searchInvoices(id, customerName, orderStatus)
				.stream()
				.map(invoiceMapper::toDTO)
				.toList();
	}

	@Override
	public InvoiceDto saveInvoice(InvoiceDto invoiceDto) {
		Invoice invoice = invoiceMapper.toEntity(invoiceDto);

		if (invoice.getInvoiceDetails() != null) {
			invoice.getInvoiceDetails().forEach(detail -> {
				ProductDto product = productClient.getProductById(detail.getProductId());
				if (product == null) {
					throw new RuntimeException("Product not found with ID: " + detail.getProductId());
				}
				detail.setInvoice(invoice);
			});
		}

		return invoiceMapper.toDTO(invoiceRepository.save(invoice));
	}
}
