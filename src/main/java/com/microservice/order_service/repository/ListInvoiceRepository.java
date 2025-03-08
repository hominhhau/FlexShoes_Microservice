package com.microservice.order_service.repository;

import com.flexshoes.flexshoesbackend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListInvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByCustomerCustomerId(Integer customerId);
}
