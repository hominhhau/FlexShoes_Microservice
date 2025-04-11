package com.microservice.payment_service.service.impl;

import com.microservice.payment_service.dto.PaymentVNPayDetailDto;
import com.microservice.payment_service.entity.PaymentVNPayDetail;
//import com.microservice.payment_service.mapper.PaymentVNPayDetailMapper;
import com.microservice.payment_service.repository.PaymentVNPayDetailRepository;
import com.microservice.payment_service.service.PaymentVNPayDetailService;
import com.microservice.payment_service.mapper.PaymentVNPayDetailMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentVNPayDetailServiceImpl implements PaymentVNPayDetailService {

    private final PaymentVNPayDetailRepository payDetailRepository;
    private final PaymentVNPayDetailMapper payDetailMapper;

    public PaymentVNPayDetailServiceImpl(PaymentVNPayDetailRepository payDetailRepository, PaymentVNPayDetailMapper payDetailMapper) {
        this.payDetailRepository = payDetailRepository;
        this.payDetailMapper = payDetailMapper;
    }

    @Override
    public PaymentVNPayDetail createPaymentVNPayDetail(PaymentVNPayDetailDto paymentVNPayDetailDto) {
        System.out.println("paymentVNPayDetailDto"+paymentVNPayDetailDto.toString());
        PaymentVNPayDetail paymentVNPayDetail = payDetailMapper.toPaymentVNPayDetail(paymentVNPayDetailDto);
        return payDetailRepository.save(paymentVNPayDetail);

    }


}
