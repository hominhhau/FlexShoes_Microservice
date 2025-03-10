package com.microservice.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_vnpay_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentVNPayDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    private String transactionId;

    private String bankCode;

    private LocalDateTime paymentTime;

    private String vnPayResponse;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
