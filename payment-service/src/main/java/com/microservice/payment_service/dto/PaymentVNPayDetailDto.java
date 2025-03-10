package com.microservice.payment_service.dto;

import com.microservice.payment_service.entity.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVNPayDetailDto {

    private int paymentId;

    private String transactionId;

    private String bankCode;

    private LocalDateTime paymentTime;

    private String vnPayResponse;

    private Payment payment;
}
