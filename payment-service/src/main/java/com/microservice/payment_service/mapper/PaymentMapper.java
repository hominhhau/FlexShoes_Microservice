package com.microservice.payment_service.mapper;

import com.microservice.payment_service.dto.PaymentDto;
import com.microservice.payment_service.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDto paymentToPaymentDto(Payment payment);
    Payment paymentDtoToPayment(PaymentDto paymentDto);

}
