package com.microservice.payment_service.mapper;

import com.microservice.payment_service.dto.PaymentDto;
import com.microservice.payment_service.entity.Payment;
import com.microservice.payment_service.entity.PaymentMethod;
import com.microservice.payment_service.entity.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto paymentToPaymentDto(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        PaymentDto paymentDto = new PaymentDto();

        paymentDto.setPaymentId( payment.getPaymentId() );
        paymentDto.setOrderId( payment.getOrderId() );
        if ( payment.getPaymentMethod() != null ) {
            paymentDto.setPaymentMethod( payment.getPaymentMethod().name() );
        }
        if ( payment.getStatus() != null ) {
            paymentDto.setStatus( payment.getStatus().name() );
        }

        return paymentDto;
    }


    public Payment paymentDtoToPayment(PaymentDto paymentDto) {
        if ( paymentDto == null ) {
            return null;
        }

        Payment payment = new Payment();

        payment.setPaymentId( paymentDto.getPaymentId() );
        payment.setOrderId( paymentDto.getOrderId() );
        if ( paymentDto.getPaymentMethod() != null ) {
            payment.setPaymentMethod( Enum.valueOf( PaymentMethod.class, paymentDto.getPaymentMethod() ) );
        }
        if ( paymentDto.getStatus() != null ) {
            payment.setStatus( Enum.valueOf( PaymentStatus.class, paymentDto.getStatus() ) );
        }

        return payment;
    }
}
