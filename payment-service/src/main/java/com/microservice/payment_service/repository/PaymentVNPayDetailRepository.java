package com.microservice.payment_service.repository;

import com.microservice.payment_service.entity.Payment;
import com.microservice.payment_service.entity.PaymentVNPayDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentVNPayDetailRepository extends JpaRepository<PaymentVNPayDetail, Integer> {

}
