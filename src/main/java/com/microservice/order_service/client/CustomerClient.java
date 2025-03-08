package com.microservice.order_service.client;

import com.flexshoes.flexshoesbackend.dto.CustomerDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerClient {
    private final RestTemplate restTemplate;

    public CustomerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CustomerDto getCustomerById(Integer customerId) {
        String url = "http://CUSTOMER-SERVICE/api/customers/" + customerId;
        return restTemplate.getForObject(url, CustomerDto.class);
    }
}