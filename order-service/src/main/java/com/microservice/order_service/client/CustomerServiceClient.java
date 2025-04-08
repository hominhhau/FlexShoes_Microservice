package com.microservice.order_service.client;

import com.microservice.order_service.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${customer-service.url}")
public interface CustomerServiceClient {

    @GetMapping("/findByID/{id}")
    CustomerDto getCustomerById(@PathVariable("id") Integer id);
}
