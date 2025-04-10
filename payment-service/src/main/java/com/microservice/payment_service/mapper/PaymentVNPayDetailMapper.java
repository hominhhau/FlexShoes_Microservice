package com.microservice.payment_service.mapper;

import com.microservice.payment_service.dto.PaymentVNPayDetailDto;
import com.microservice.payment_service.entity.PaymentVNPayDetail;
import org.springframework.stereotype.Component;


@Component
public class PaymentVNPayDetailMapper {

    public PaymentVNPayDetailDto toPaymentVNPayDetailDto(PaymentVNPayDetail payDetail) {
        if ( payDetail == null ) {
            return null;
        }

        PaymentVNPayDetailDto paymentVNPayDetailDto = new PaymentVNPayDetailDto();

        paymentVNPayDetailDto.setPaymentId( payDetail.getPaymentId() );
        paymentVNPayDetailDto.setTransactionId( payDetail.getTransactionId() );
        paymentVNPayDetailDto.setBankCode( payDetail.getBankCode() );
        paymentVNPayDetailDto.setPaymentTime( payDetail.getPaymentTime() );
        paymentVNPayDetailDto.setVnPayResponse( payDetail.getVnPayResponse() );
        paymentVNPayDetailDto.setPayment( payDetail.getPayment() );

        return paymentVNPayDetailDto;
    }


    public PaymentVNPayDetail toPaymentVNPayDetail(PaymentVNPayDetailDto payDetailDto) {
        if ( payDetailDto == null ) {
            return null;
        }

        PaymentVNPayDetail paymentVNPayDetail = new PaymentVNPayDetail();

        paymentVNPayDetail.setPaymentId( payDetailDto.getPaymentId() );
        paymentVNPayDetail.setTransactionId( payDetailDto.getTransactionId() );
        paymentVNPayDetail.setBankCode( payDetailDto.getBankCode() );
        paymentVNPayDetail.setPaymentTime( payDetailDto.getPaymentTime() );
        paymentVNPayDetail.setVnPayResponse( payDetailDto.getVnPayResponse() );
        paymentVNPayDetail.setPayment( payDetailDto.getPayment() );

        return paymentVNPayDetail;
    }
}
