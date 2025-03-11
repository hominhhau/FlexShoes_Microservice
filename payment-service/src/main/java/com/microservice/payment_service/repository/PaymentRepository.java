package com.microservice.payment_service.repository;

import com.microservice.payment_service.dto.PaymentDto;
import com.microservice.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    //find payment by oder_Id
    @Query("SELECT p FROM Payment p WHERE p.orderId = ?1")
    public Payment findByOrderId(int orderId);
    //update status payment by id
    @Query("UPDATE Payment p SET p.status = ?1 WHERE p.paymentId = ?2")
    public Boolean updateStatusByPaymentId(String status, int paymentId);


}
