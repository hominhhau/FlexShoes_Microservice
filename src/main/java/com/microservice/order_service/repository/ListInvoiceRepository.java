package com.microservice.order_service.repository;

import com.microservice.order_service.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListInvoiceRepository extends JpaRepository<Invoice, Integer> {
//    List<Invoice> findByCustomerCustomerId(Integer customerId);
}
