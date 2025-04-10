package com.microservice.payment_service.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class InvoiceDto {
    Integer invoiceId;
    double total;
}
