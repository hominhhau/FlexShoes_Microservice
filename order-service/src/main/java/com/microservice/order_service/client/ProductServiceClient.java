package com.microservice.order_service.client;

import com.microservice.order_service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${product-service.url}")
public interface ProductServiceClient {

    @GetMapping("/api/products/{productId}")
    ProductDto getProductById(@PathVariable("productId") Integer productId);
}
