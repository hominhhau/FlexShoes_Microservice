package com.microservice.payment_service.mapper;

import com.microservice.payment_service.dto.PaymentVNPayDetailDto;
import com.microservice.payment_service.entity.PaymentVNPayDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentVNPayDetailMapper {

    PaymentVNPayDetailDto toPaymentVNPayDetailDto(PaymentVNPayDetail payDetail);
    PaymentVNPayDetail toPaymentVNPayDetail(PaymentVNPayDetailDto payDetailDto);
}
