package com.microservice.payment_service.service;

import com.microservice.payment_service.dto.PaymentDto;
import com.microservice.payment_service.dto.request.InvoiceDto;
import com.microservice.payment_service.entity.Payment;

public interface PaymentService {
    public boolean createPayment(PaymentDto paymentDto);
    public boolean deletePayment(int paymentId);
    public boolean updatePayment(PaymentDto paymentDto);
    // get order by  order-service
    public InvoiceDto getInfoOrder(int orderId);
}
