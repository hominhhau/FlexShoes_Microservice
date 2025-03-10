package com.microservice.order_service.service.impl;

import com.microservice.order_service.repository.InvoiceDetailRepository;
import com.microservice.order_service.service.InvoiceDetailService;
import org.springframework.stereotype.Service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceDetailServiceImpl implements InvoiceDetailService {
	InvoiceDetailRepository invoiceDetailRepository;

	@Override
	public boolean deleteInvoiceDetailById(Integer detailId) {
		if (invoiceDetailRepository.existsById(detailId)) {
			invoiceDetailRepository.deleteById(detailId);
			return true;
		} else {
			return false;
		}

	}

}
