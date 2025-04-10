package com.microservice.payment_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentDto {
    private int paymentId;
    private int orderId;
    private String paymentMethod;
    private String status;
}
