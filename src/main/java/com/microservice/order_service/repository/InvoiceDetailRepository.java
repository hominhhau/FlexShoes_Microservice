package com.microservice.order_service.repository;

import com.flexshoes.flexshoesbackend.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {
	boolean existsById(Integer invoice);

}
