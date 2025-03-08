package com.microservice.order_service.service.impl;

import com.flexshoes.flexshoesbackend.repository.InvoiceDetailRepository;
import com.flexshoes.flexshoesbackend.service.InvoiceDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceDetailServiceImpl implements InvoiceDetailService {
	private final InvoiceDetailRepository invoiceDetailRepository;

	@Override
	public boolean deleteInvoiceDetailById(Integer detailId) {
		if (!invoiceDetailRepository.existsById(detailId)) {
			log.warn("InvoiceDetail with ID {} not found!", detailId);
			return false;
		}

		invoiceDetailRepository.deleteById(detailId);
		log.info("InvoiceDetail with ID {} deleted successfully.", detailId);
		return true;
	}
}
