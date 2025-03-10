package com.microservice.payment_service.service;

import com.microservice.payment_service.dto.PaymentVNPayDetailDto;
import com.microservice.payment_service.entity.PaymentVNPayDetail;

public interface PaymentVNPayDetailService {

    public PaymentVNPayDetail createPaymentVNPayDetail (PaymentVNPayDetailDto paymentVNPayDetailDto);

}
