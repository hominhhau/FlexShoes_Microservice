package com.microservice.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Integer productId;
    private String productName;
    private double salePrice;
    private double finalPrice;
    private String status;
    private List<String> images;
    private double originalPrice;

}
