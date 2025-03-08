package com.microservice.order_service.client;
import com.flexshoes.flexshoesbackend.dto.ProductDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductClient {
    private final RestTemplate restTemplate;

    public ProductClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductDto getProductById(Integer productId) {
        String url = "http://PRODUCT-SERVICE/api/products/" + productId;
        return restTemplate.getForObject(url, ProductDto.class);
    }

    public RestOperations getRestTemplate() {
        return restTemplate;
    }
}
