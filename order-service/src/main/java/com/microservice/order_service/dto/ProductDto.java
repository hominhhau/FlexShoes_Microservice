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
    private String productId;
    private String productName;
    private double discount;
    private String status;
    private List<String> images;
    private double sellingPrice;
    private String description;
    private Integer totalQuantity;
    private boolean gender;
    private Integer tax;

}
