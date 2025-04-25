package com.microservice.order_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    @JsonProperty("_id")
    private String productId;
    private String productName;
    private double discount;
    private String status;
    private List<ImageInfo> image = new ArrayList<>();
    private double sellingPrice;
    private String description;
    private Integer totalQuantity;
    private String gender;
    private Integer tax;

}
