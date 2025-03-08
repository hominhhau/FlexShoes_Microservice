package com.microservice.order_service.repository;

import com.flexshoes.flexshoesbackend.client.ProductClient;
import com.flexshoes.flexshoesbackend.dto.ProductDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {
    private final ProductClient productClient;

    public ProductRepository(ProductClient productClient) {
        this.productClient = productClient;
    }

    // Tìm sản phẩm theo ID bằng cách gọi ProductClient
    public ProductDto findByProductId(Integer productId) {
        return productClient.getProductById(productId);
    }

    // Tìm kiếm sản phẩm theo tên (cần có API hỗ trợ trong ProductService)
    public List<ProductDto> findByNameContainingIgnoreCase(String productName) {
        // Giả sử ProductService có API `/api/products/search?name=...`
        String url = "http://PRODUCT-SERVICE/api/products/search?name=" + productName;
        return List.of(productClient.getRestTemplate().getForObject(url, ProductDto[].class));
    }
}
