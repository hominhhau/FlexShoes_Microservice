package com.microservice.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Integer customerId;
    private String customerName;
    private String phoneNumber;
    private String email;
    private String gender;
    private LocalDate registerDate;
    private Set<String> address;
}
