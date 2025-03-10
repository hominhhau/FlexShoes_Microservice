package com.microservice.payment_service.service.impl;

import com.microservice.payment_service.dto.PaymentVNPayDetailDto;
import com.microservice.payment_service.entity.PaymentVNPayDetail;
//import com.microservice.payment_service.mapper.PaymentVNPayDetailMapper;
import com.microservice.payment_service.repository.PaymentVNPayDetailRepository;
import com.microservice.payment_service.service.PaymentVNPayDetailService;
import com.microservice.payment_service.mapper.PaymentVNPayDetailMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentVNPayDetailServiceImpl implements PaymentVNPayDetailService {

    private PaymentVNPayDetailRepository payDetailRepository;
    private com.microservice.payment_service.mapper.PaymentVNPayDetailMapper payDetailMapper;

    @Override
    public PaymentVNPayDetail createPaymentVNPayDetail(PaymentVNPayDetailDto paymentVNPayDetailDto) {
        PaymentVNPayDetail paymentVNPayDetail = payDetailMapper.toPaymentVNPayDetail(paymentVNPayDetailDto);
        return payDetailRepository.save(paymentVNPayDetail);

    }
}
