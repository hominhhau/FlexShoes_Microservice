package com.microservice.order_service.repository;

import com.microservice.order_service.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ListInvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByCustomerId(Integer customerId);
}
